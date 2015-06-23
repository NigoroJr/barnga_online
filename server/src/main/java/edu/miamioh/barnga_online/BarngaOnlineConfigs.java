package edu.miamioh.barnga_online;

import java.util.HashSet;

/**
 * Interface that defines the callback methods and initialization methods that
 * are needed for the game.
 *
 * @author Naoki Mizuno
 */
public interface BarngaOnlineConfigs {
    /**
     * Sets the initial coordinate of the player.
     *
     * @param playerId the player that needs a new place to start
     *
     * @param teamId the team that the player belongs to
     *
     * @return the new coordinates for the player
     */
    public Coordinates initialCoordinates(long playerId, int teamId);

    /**
     * Assigns a team that the player belongs to.
     *
     * @return the team ID of the team that the player belongs to
     */
    public int assignTeam(int playerId);

    /**
     * Determines when the game should start.
     */
    public boolean gameStarts();

    /**
     * Determines whether the game should end.
     */
    public boolean gameEnds();

    /**
     * Callback method when bumping into a different player.
     *
     * This method does not need to do anything if there is no particular
     * action that needs to be executed when bumping into a different player.
     * The check for whether the bumper can move is done in playerMovable
     * method.
     *
     * @param bumper the player who bumped into the other player. In other
     * words, the player who requested the move.
     *
     * @param bumpee the player who got bumped into
     */
    public void bumpPlayerCallback(Player bumper, Player bumpee);

    /**
     * Callback method when bumping into a food.
     *
     * This method does not need to do anything if there is no particular
     * action that needs to be executed when bumping into a food.
     * The check for whether the player can eat the food is done in
     * foodEatable method.
     *
     * @param player the player who bumped into the food
     *
     * @param food the food that the player bumped into
     */
    public void bumpFoodCallback(Player player, Food food);

    /**
     * Callback method when player collects food.
     *
     * This method does NOT handle the points. handlePoints() method is called
     * when a player eats a food, so this method does not need to do that.
     * This method Typically only removes the food (regenerating the food is
     * done in generateFood() method).
     *
     * @param player the player who bumped into the food
     *
     * @param food the food that the player bumped into
     */
    public void eatFoodCallback(Player player, Food food);

    /**
     * Generator for foods.
     *
     * @param player the player who ate the food previously
     *
     * @param food the food that was eaten previously
     *
     * @return HashSet of Food that was added to the world. null if nothing
     *         was added.
     */
    public HashSet<Food> generateFood(Player player, Food food);

    /**
     * Callback method when receiving request for moving coordinates.
     *
     * Typically, this method should check whether the coordinates are within
     * the world boundaries and whether there is some other player already
     * at the coordinates.
     *
     * @param player the player who is trying to move
     *
     * @param newCoord the coordinates to which the player is trying to move
     *
     * @return true if player can move to the coordinates
     */
    public boolean playerMovable(Player player, Coordinates newCoord);

    /**
     * Determine whether a player can see a food.
     *
     * @param player
     *
     * @param food
     *
     * @return true if player can see the food, false if not
     */
    public boolean foodVisible(Player player, Food food);

    /**
     * Handles points-related behavior when getting food.
     *
     * @param player the player who ate the food
     *
     * @param food the food that was eaten
     */
    public void handlePoints(Player player, Food food);
}
