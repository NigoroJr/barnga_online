package edu.miamioh.barnga_online.listeners;

import java.util.Set;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.listener.ConnectListener;

import edu.miamioh.barnga_online.*;
import edu.miamioh.barnga_online.events.*;

public class ConnectEventListener implements ConnectListener {
    protected WorldState world;
    protected BarngaOnlineConfigs configs;
    protected SocketIOServer server;

    public ConnectEventListener(BarngaOnlineConfigs configs, WorldState world,
            SocketIOServer server) {
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
                (BarngaOnlineConfigsDefault)configs);
        world.addPlayer(p, teamId, client);

        // Add to room (for broadcasting)
        SocketIONamespace team = server.getNamespace(Integer.toString(teamId));
        if (team == null) {
            Util.debug("Adding Team %d to world\n", teamId);
            server.addNamespace(Integer.toString(teamId));
        }
        client.joinRoom(Integer.toString(teamId));

        Util.debug("Player ID %d connected at %s\n", playerId, coord);

        // Send player's identity
        client.sendEvent(Constants.EVENT_PLAYER_ID, new MessagePlayerId(p));

        // Send visible food information
        for (Food f : world.getFoods().values()) {
            if (p.canSee(f)) {
                Food fakeFood = new Food(f);
                fakeFood.team = f.appearsTo(p);

                MessageFoodCoord mes =
                    new MessageFoodCoord(fakeFood, f.coord, false);
                client.sendEvent(Constants.EVENT_FOOD_UPDATE, mes);
            }
        }

        Util util = new Util(world, (BarngaOnlineConfigsDefault)configs);
        // Broadcast about existing players to new player
        for (Team<Player> t : world.getTeams().values()) {
            for (Player otherPlayer : t) {
                // If the new player can't see the existing player
                if (!p.canSee(otherPlayer)) {
                    continue;
                }

                // Fake player (potentially with incorrect team information)
                MessagePlayerCoord mes =
                    util.makeFakePlayerMessage(p, otherPlayer, otherPlayer.coord);

                // Broadcast to one team
                client.sendEvent(Constants.EVENT_PLAYER_UPDATE, mes);
            }
        }
        Util.debug("Sent to %d about existing players\n", playerId);

        // Send message to currently existing teams
        for (Team<Player> t : world.getTeams().values()) {
            if (!t.canSee(p)) {
                continue;
            }

            // Broadcast TO existing player
            MessagePlayerCoord mes = util.makeFakePlayerMessage(t, p, p.coord);
            String roomName = Integer.toString(t.getTeamId());
            BroadcastOperations room = server.getRoomOperations(roomName);
            room.sendEvent(Constants.EVENT_PLAYER_UPDATE, mes);

            Util.debug("Sent to %d about the new player %d\n", t.getTeamId(), playerId);
        }

        sendPoints(client);

        if (world.isGameStarted()) {
            client.sendEvent(Constants.EVENT_GAME_START);
        }
        else if (!world.isGameStarted() && configs.gameStarts()) {
            server.getBroadcastOperations().sendEvent(Constants.EVENT_GAME_START);
            world.setGameStarted(true);
            Util.debug("Game has started!");
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
}
