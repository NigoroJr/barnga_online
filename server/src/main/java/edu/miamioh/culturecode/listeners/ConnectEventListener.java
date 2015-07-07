package edu.miamioh.culturecode.listeners;

import java.util.Set;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;

import edu.miamioh.culturecode.CulturecodeConfigsDefault;
import edu.miamioh.culturecode.Constants;
import edu.miamioh.culturecode.Coordinates;
import edu.miamioh.culturecode.Food;
import edu.miamioh.culturecode.Player;
import edu.miamioh.culturecode.Points;
import edu.miamioh.culturecode.Team;
import edu.miamioh.culturecode.Util;
import edu.miamioh.culturecode.WorldState;
import edu.miamioh.culturecode.events.MessageFoodCoord;
import edu.miamioh.culturecode.events.MessagePlayerCoord;
import edu.miamioh.culturecode.events.MessagePlayerId;
import edu.miamioh.culturecode.events.MessagePointsUpdate;
import edu.miamioh.culturecode.events.MessageWorldParams;

public class ConnectEventListener implements ConnectListener {
    protected WorldState world;
    protected CulturecodeConfigsDefault configs;
    protected SocketIOServer server;

    public ConnectEventListener(CulturecodeConfigsDefault configs,
            WorldState world, SocketIOServer server) {
        super();

        this.world = world;
        this.configs = configs;
        this.server = server;
    }

    @Override
    public void onConnect(SocketIOClient client) {
        // Assign player to a team
        int playerId = world.getId();
        int teamId = configs.assignTeam(playerId);
        Coordinates coord = configs.initialCoordinates(playerId, teamId);

        Player p = new Player(playerId, teamId, coord,
                configs);
        // Make sure to associate player with the client
        world.addPlayer(p, teamId, client);
        Util.debug("Player ID %d connected at %s\n", playerId, coord);
        Util.debug("We have %d players\n", world.getPlayers().size());

        // Add to room (for broadcasting)
        SocketIONamespace team = server.getNamespace(Integer.toString(teamId));
        if (team == null) {
            Util.debug("Adding Team %d to world\n", teamId);
            server.addNamespace(Integer.toString(teamId));
        }
        client.joinRoom(Integer.toString(teamId));

        // Initialize points if it's first time adding points
        if (!world.getPoints().containsKey(teamId)) {
            world.getPoints().put(teamId, new Points());
        }

        // Tell the client about world size etc.
        sendWorldParams(client, teamId);
        // Send player's identity
        client.sendEvent(Constants.EVENT_PLAYER_ID, new MessagePlayerId(p));
        // Generate food for player's team
        configs.onConnectCallback(p);

        sendFoods(p, client);
        sendPlayers(p, client);
        sendPoints(client);

        // Handle game start
        if (world.isGameStarted()) {
            client.sendEvent(Constants.EVENT_GAME_START);
        }
        else if (!world.isGameStarted() && configs.gameStarts()) {
            server.getBroadcastOperations().sendEvent(Constants.EVENT_GAME_START);
            world.setGameStarted(true);
            Util.debug("Game has started!");
        }
    }

    private void sendPlayers(Player player, SocketIOClient client) {
        Util util = new Util(world, configs);
        // Broadcast about existing players to new player
        for (Team<Player> t : world.getTeams().values()) {
            for (Player otherPlayer : t) {
                // If the new player can't see the existing player
                if (!player.canSee(otherPlayer)) {
                    continue;
                }

                // Fake player (potentially with incorrect team information)
                MessagePlayerCoord mes = util.makeFakePlayerMessage(
                        player, otherPlayer, otherPlayer.coord);

                // Broadcast to one team
                client.sendEvent(Constants.EVENT_PLAYER_UPDATE, mes);
            }
        }

        // Send message to currently existing teams
        for (Team<Player> t : world.getTeams().values()) {
            if (!t.canSee(player)) {
                continue;
            }

            // Broadcast TO existing player
            MessagePlayerCoord mes =
                util.makeFakePlayerMessage(t, player, player.coord);
            String roomName = Integer.toString(t.getTeamId());
            BroadcastOperations room = server.getRoomOperations(roomName);
            room.sendEvent(Constants.EVENT_PLAYER_UPDATE, mes);
        }
    }

    /**
     * Sends to the newly connected client about the current status of the
     * food.
     */
    private void sendFoods(Player player, SocketIOClient client) {
        // Send message to currently existing teams including the new player
        for (Team<Player> t : world.getTeams().values()) {
            for (Food f : world.getFoods().values()) {
                if (!t.canSee(f)) {
                    continue;
                }

                // Broadcast to existing teams
                Food fakeFood = new Food(f);
                fakeFood.team = f.appearsTo(t);

                MessageFoodCoord mes =
                    new MessageFoodCoord(fakeFood, f.coord, false);
                String roomName = Integer.toString(t.getTeamId());
                BroadcastOperations room = server.getRoomOperations(roomName);
                room.sendEvent(Constants.EVENT_FOOD_UPDATE, mes);
            }
        }
    }

    /**
     * Sends the current points status to the newly connected client.
     *
     * @param client the client that connected
     */
    private void sendPoints(SocketIOClient client) {
        Set<Integer> teamIds = world.getPoints().keySet();
        for (int id : teamIds) {
            int currentPoints = world.getPoints().get(id).getTotal();
            MessagePointsUpdate mes = new MessagePointsUpdate(id, currentPoints);

            client.sendEvent(Constants.EVENT_POINTS_UPDATE, mes);
        }
    }

    private void sendWorldParams(SocketIOClient client, int teamId) {
        MessageWorldParams mes = new MessageWorldParams(
                configs.getWorldSizeX(),
                configs.getWorldSizeY(),
                configs.getGridSize(),
                configs.getSpeed(teamId));

        client.sendEvent(Constants.EVENT_WORLD_PARAMS, mes);
    }
}
