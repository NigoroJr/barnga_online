package edu.miamioh.barnga_online;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

import com.corundumstudio.socketio.SocketIOClient;

public class WorldState {
    /* teamId => Team, which is a HashSet of Player */
    protected HashMap<Integer, Team<Player>> teams;
    /* foodId => Food */
    protected HashMap<Integer, Food> foods;
    /* teamId => Points */
    protected HashMap<Integer, Points> points;

    protected int worldSizeX, worldSizeY;
    /* Size of the field viewable to the players on the client side */
    protected int viewSizeX, viewSizeY;

    /* Associates SocketIOClient to Player */
    protected HashMap<SocketAddress, Player> clients;

    protected BarngaOnlineConfigsDefault configs;

    protected boolean gameStarted;
    private int maxId = 0;

    /**
     * Default constructor.
     */
    public WorldState() {
        teams = new HashMap<Integer, Team<Player>>();
        foods = new HashMap<Integer, Food>();
        points = new HashMap<Integer, Points>();
        clients = new HashMap<SocketAddress, Player>();
        gameStarted = false;
    }

    /**
     * Returns the dictionary of all teams.
     *
     * @return Dictionary (key: team ID, value: HashSet of Player)
     */
    public HashMap<Integer, Team<Player>> getTeams() {
        return teams;
    }

    /**
     * Returns the corresponding team object for the given ID.
     *
     * @return the Team object. null if team doesn't exist.
     */
    public Team<Player> getTeam(int teamId) {
        return teams.get(teamId);
    }

    /**
     * Returns the dictionary of all players.
     *
     * The keys are the player's ID, and the values are the Player object.
     *
     * @return Dictionary (key: player ID, value: Player) of all players
     */
    public HashMap<Integer, Player> getPlayers() {
        HashMap<Integer, Player> players = new HashMap<Integer, Player>();
        for (Team<Player> t : teams.values()) {
            for (Player p : t) {
                players.put(p.id, p);
            }
        }
        return players;
    }

    /**
     * Returns the dictionary of all foods.
     *
     * The keys are the food's ID, and the values are the Food object.
     *
     * @return Dictionary (key: food ID, valu: Food) of all food
     */
    public HashMap<Integer, Food> getFoods() {
        return foods;
    }

    /**
     * Returns the dictionary of points of all teams.
     *
     * The keys are the team's ID, and the values are the total points for
     * that team.
     *
     * TODO: getPoints(int teamId) to get the points for one team
     *
     * @return Dictionary (key: team ID, value: total team point) of all teams
     */
    public HashMap<Integer, Points> getPoints() {
        return points;
    }

    /**
     * Adds a new player to the world.
     *
     * This method will update the collection of teams if the given teamId
     * does not exist in the current collection of teams. Therefore, there is
     * no need for teams to be explicitly added.
     *
     * @param player the new player to be added
     *
     * @param teamId the team that the player belongs to
     */
    public void addPlayer(Player player, int teamId) {
        if (!teams.containsKey(teamId)) {
            teams.put(teamId, new Team<Player>(teamId, player.getConfigs()));
        }

        Team<Player> playerTeam = teams.get(teamId);
        playerTeam.add(player);
    }

    public void addPlayer(Player player, int teamId,
            SocketIOClient client) {
        addPlayer(player, teamId);
        clients.put(client.getRemoteAddress(), player);
    }

    /**
     * Adds a new food to the world.
     *
     * @param food the new food to be added
     */
    public void addFood(Food food) {
        foods.put(food.id, food);
    }

    /**
     * Removes a player from the world.
     *
     * This method does nothing when the given user is not found in the world.
     *
     * @param player the player to be removed
     */
    public void removePlayer(Player player) {
        int teamId = player.teamId;
        teams.get(teamId).remove(player);

        // Find the corresponding SocketAddress of this player
        for (SocketAddress addr : getClients().keySet()) {
            if (getClients().get(addr).equals(player)) {
                getClients().remove(addr);
                break;
            }
        }
    }

    /**
     * Removes a food from the world.
     *
     * This method does nothing when the given food is not found in the world.
     *
     * @param food the food to be removed
     */
    public void removeFood(Food food) {
        foods.remove(food.id);
    }

    public int getWorldSizeX() {
        return worldSizeX;
    }

    public int getWorldSizeY() {
        return worldSizeY;
    }

    public void setWorldSizeX(int worldSizeX) {
        this.worldSizeX = worldSizeX;
    }

    public void setWorldSizeY(int worldSizeY) {
        this.worldSizeY = worldSizeY;
    }

    public int getViewSizeX() {
        return viewSizeX;
    }

    public int getViewSizeY() {
        return viewSizeY;
    }

    public void setViewSizeX(int viewSizeX) {
        this.viewSizeX = viewSizeX;
    }

    public void setViewSizeY(int viewSizeY) {
        this.viewSizeY = viewSizeY;
    }

    /**
     * Returns whether there *actually* is a player at a coordinate.
     *
     * Note that this method does not care about the visibility of the player.
     *
     * @return the player at the given coordinate. null if no player exists.
     */
    public Player playerAt(Coordinates coord) {
        for (Player p : getPlayers().values()) {
            if (p.coord.equals(coord)) {
                return p;
            }
        }

        return null;
    }

    /**
     * Returns a player visible to the given player near the given
     * coordinates.
     *
     * The returned player is NOT necessarily the closest player.
     */
    public Player visiblePlayerNear(Player player, Coordinates baseCoord,
            double validDistance) {
        for (Player p : playersNear(baseCoord, validDistance)) {
            if (!player.equals(p) && player.canSee(p)) {
                return p;
            }
        }

        return null;
    }

    /**
     * Returns a visible player near the given player.
     *
     * The returned player is NOT necessarily the closest player.
     *
     * @param player the player
     *
     * @param validDistance what's considered "near"
     *
     * @return one of the players that are near the given player. Returns null
     *         if no player is around.
     */
    public Player visiblePlayerNear(Player player, double validDistance) {
        return visiblePlayerNear(player, player.coord, validDistance);
    }

    /**
     * Gets all the players that near the given coordinates.
     *
     * @param coord the coordinates to be searched around
     *
     * @param validDistance what's considered "near"
     *
     * @return a list of all the players that are within validDistance from
     *         the coordinates. An empty list is returned when no player is
     *         near the coordinates. Note that this method does not care about
     *         the visibility of the players.
     *
     * @see visiblePlayerNear
     */
    public ArrayList<Player> playersNear(Coordinates coord,
            double validDistance) {
        ArrayList<Player> players = new ArrayList<Player>();
        for (Player p : getPlayers().values()) {
            double distance = Util.distance(p.coord, coord);
            if (distance <= validDistance) {
                players.add(p);
            }
        }

        return players;
    }

    /**
     * Returns whether there *actually* is a food at a coordinate.
     *
     * Note that this method does not care about the visibility of the food.
     *
     * @return the food at the given coordinate. null if no food exists.
     */
    public Food foodAt(Coordinates coord) {
        for (Food f : foods.values()) {
            if (f.coord.equals(coord)) {
                return f;
            }
        }

        return null;
    }

    /**
     * Returns one of the food that are near the given player.
     *
     * @param player the player to be searched around
     *
     * @param validDistance what's considered "near"
     *
     * @return one of the food that's eatable by and near the player. null if
     *         there is no such food.
     */
    public Food eatableFoodNear(Player player, double validDistance) {
        for (Food f : foodsNear(player.coord, validDistance)) {
            if (player.canEat(f)) {
                return f;
            }
        }

        return null;
    }

    /**
     * Returns all the food near the given coordinates.
     *
     * Note that this method does not care about the visibility of the food.
     *
     * @param coord the coordinates to be checked around
     *
     * @param validDistance what's considered "near"
     *
     * @return the list of food that is near the given coordinates. An empty
     *         list is returned if no such food.
     */
    public ArrayList<Food> foodsNear(Coordinates coord, double validDistance) {
        ArrayList<Food> foods = new ArrayList<Food>();
        for (Food f : getFoods().values()) {
            double distance = Util.distance(f.coord, coord);
            if (distance <= validDistance) {
                foods.add(f);
            }
        }

        return foods;
    }

    /**
     * Returns a fresh ID.
     *
     * @return an int that can be used as an ID
     */
    public int getId() {
        int ret = maxId;
        maxId++;
        return ret;
    }

    /**
     * Returns whether the game is started in the world.
     */
    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public HashMap<SocketAddress, Player> getClients() {
        return clients;
    }
}
