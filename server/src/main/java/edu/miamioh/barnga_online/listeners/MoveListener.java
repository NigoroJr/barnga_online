package edu.miamioh.barnga_online.listeners;

import java.util.HashSet;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

import edu.miamioh.barnga_online.BarngaOnlineConfigsDefault;
import edu.miamioh.barnga_online.Constants;
import edu.miamioh.barnga_online.Coordinates;
import edu.miamioh.barnga_online.Food;
import edu.miamioh.barnga_online.Player;
import edu.miamioh.barnga_online.Team;
import edu.miamioh.barnga_online.Util;
import edu.miamioh.barnga_online.WorldState;
import edu.miamioh.barnga_online.events.MessageFoodCoord;
import edu.miamioh.barnga_online.events.MessagePlayerCoord;
import edu.miamioh.barnga_online.events.MessagePointsUpdate;

/**
 * Listeners for when receiving the EVENT_MOVE event.
 *
 * @author Naoki Mizuno
 */
public class MoveListener implements DataListener<MessagePlayerCoord> {
    protected WorldState world;
    protected BarngaOnlineConfigsDefault configs;
    protected SocketIOServer server;

    public MoveListener(BarngaOnlineConfigsDefault configs,
            WorldState world, SocketIOServer server) {
        super();

        this.world = world;
        this.configs = configs;
        this.server = server;
    }

    @Override
    public void onData(SocketIOClient client, MessagePlayerCoord message,
            AckRequest req) {
        Player player = new Player(message.player, configs);
        Coordinates newCoord = message.newCoord;

        Util.debug("Request from Player ID %d of Team %d from %s to %s\n",
                player.id, player.teamId,
                player.coord.toString(), newCoord.toString());

        // Handle move
        handlePlayer(player, newCoord);

        // Handle food
        handleFood(player, newCoord);

        if (configs.gameEnds()) {
            Util.debug("Game ended!");
            server.getBroadcastOperations().sendEvent(Constants.EVENT_GAME_END);
            // Reset world
            world.reset();
        }
    }

    /**
     * Handles the player's move-related response on receiving data.
     */
    private void handlePlayer(Player player, Coordinates newCoord) {
        Player otherPlayer =
                world.visiblePlayerNear(player, configs.getGridSize());

        // Someone's there
        if (otherPlayer != null) {
            configs.bumpPlayerCallback(player, otherPlayer);
        }

        if (!configs.playerMovable(player, newCoord)) {
            return;
        }

        // Broadcast to team
        for (Team<Player> t : player.seenBy()) {
            // Create fake player (potentially with incorrect team information)
            Player fakePlayer = new Player(player);
            fakePlayer.teamId = fakePlayer.appearsTo(t);
            MessagePlayerCoord mes = new MessagePlayerCoord(fakePlayer, newCoord);

            // Broadcast to one team
            BroadcastOperations team = server.getRoomOperations(
                    Integer.toString(t.getTeamId()));
            team.sendEvent(Constants.EVENT_PLAYER_UPDATE, mes);
        }

        // Update the player's coordinate
        player.coord = newCoord;
    }

    /**
     * Handles the food-related response on receiving player move.
     */
    private void handleFood(Player player, Coordinates newCoord) {
        Food food;
        // Check for nearby food
        food = world.visibleFoodNear(player, configs.getGridSize());
        if (food == null) {
            return;
        }
        configs.bumpFoodCallback(player, food);

        // Check for nearby eatable food
        food = world.eatableFoodNear(player, configs.getGridSize());
        // No food eatable
        if (food == null) {
            return;
        }

        // Note: Most likely food will be removed from world here
        configs.eatFoodCallback(player, food);
        // Broadcast about the point change
        configs.handlePoints(player, food);
        broadcastPoints(player.teamId);

        // Note: And potentially get generated here
        HashSet<Food> addedFood = configs.generateFood(player, food);

        // First, broadcast whether the food that was taken
        boolean gone = world.getFoods().get(food.id) == null;
        broadcastFood(food, gone);

        // Then, broadcast about the newly-generated food (if any)
        if (addedFood != null && !addedFood.isEmpty()) {
            for (Food f : addedFood) {
                broadcastFood(f, false);
            }
        }

        Util.debug("Food change broadcasting finished");
    }

    /**
     * Broadcasts to the relevant teams about the food change.
     *
     * This method finds out who the relevant teams are, and what the given
     * food appears to them.
     *
     * @param food the Food to be broadcasted
     *
     * @param gone whether the food is gone from the world or not
     */
    private void broadcastFood(Food food, boolean gone) {
        for (Team<Player> t : food.seenBy()) {
            // Make fake Food where belonging team is modified
            Food fakeFood = new Food(food);
            fakeFood.team = food.appearsTo(t);
            MessageFoodCoord mes = new MessageFoodCoord(fakeFood, fakeFood.coord, gone);

            // Broadcast to one team
            BroadcastOperations team = server.getRoomOperations(
                    Integer.toString(t.getTeamId()));
            team.sendEvent(Constants.EVENT_FOOD_UPDATE, mes);
        }
    }

    /**
     * Broadcasts the changed points status to all the teams.
     *
     * @param teamId the ID of the team whose points were changed
     */
    private void broadcastPoints(int teamId) {
        int newPoint = world.getPoints().get(teamId).getTotal();
        MessagePointsUpdate mes = new MessagePointsUpdate(teamId, newPoint);

        server.getBroadcastOperations().sendEvent(
                Constants.EVENT_POINTS_UPDATE, mes);
    }
}
