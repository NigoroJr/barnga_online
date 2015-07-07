package edu.miamioh.barnga_online;

import java.util.HashSet;

/**
 * Default configuration for the game.
 *
 * @author Naoki Mizuno
 */
public class BarngaOnlineConfigsDefault implements BarngaOnlineConfigs {
    /* Invisible */
    public static final int INVISIBLE = -1;
    // Just an abbreviation to make the table look nicer
    public static final int I = INVISIBLE;
    /* Eatable? */
    public static final int T = 1;
    public static final int F = 0;

    /* Default values */
    public static final int DEFAULT_WORLD_X = 3000;
    public static final int DEFAULT_WORLD_Y = 3000;
    public static final int DEFAULT_GRID_SIZE = 25;
    /* Used when assigning team */
    public static final int DEFAULT_TEAM_NUMBER = 4;
    public static final int DEFAULT_FOOD_PER_PLAYER = 10;
    public static final int DEFAULT_MAXIMUM_FOOD_PER_TEAM = 100;
    public static final int DEFAULT_MAXIMUM_POINTS = 100;
    /* How many points a player gets when eating a food */
    public static final int DEFAULT_OWN_TEAM_FOOD_POINTS = 3;
    public static final int DEFAULT_OTHER_TEAM_FOOD_POINTS = 2;
    public static final int DEFAULT_FOOD_REGENERATION = 0;
    public static final int[][] DEFAULT_PLAYER_VISIBILITY = {
        {0, I, I, I},   // Team 0: Doesn't see any other team
        {2, 1, 3, 0},   // Team 1: Sees different from actual
        {0, 1, 2, 3},   // Team 2: Sees all other teams
        {I, 1, I, 3},   // Team 3: Only sees some teams
    };
    public static final int[][] DEFAULT_FOOD_EATABILITY = {
        {T, F, F, F},
        {F, T, F, F},
        {F, F, T, F},
        {F, F, F, T},
    };
    public static final int[][] DEFAULT_FOOD_VISIBILITY = {
        {0, I, 2, I},   // Team 0: Sees some of other teams' food
        {I, 1, I, I},   // Team 1: All food of their own
        {0, 1, 2, 3},   // Team 2: Sees food for all other teams
        {3, 3, 3, 3},   // Team 3: Everything looks the same
    };

    /* Will get updated on reading configuration file */
    /* Note: These are NOT constants (even though they act like them) */
    protected int WORLD_X = DEFAULT_WORLD_X;
    protected int WORLD_Y = DEFAULT_WORLD_Y;
    /* Grid size of clients */
    protected int CLIENT_GRID_SIZE = DEFAULT_GRID_SIZE;
    protected int TEAM_NUMBER = DEFAULT_TEAM_NUMBER;
    protected int FOOD_PER_PLAYER = DEFAULT_FOOD_PER_PLAYER;
    protected int MAXIMUM_FOOD_PER_TEAM = DEFAULT_MAXIMUM_FOOD_PER_TEAM;
    protected int OWN_TEAM_FOOD_POINTS = DEFAULT_OWN_TEAM_FOOD_POINTS;
    protected int OTHER_TEAM_FOOD_POINTS = DEFAULT_OTHER_TEAM_FOOD_POINTS;
    protected int FOOD_REGENERATION = DEFAULT_FOOD_REGENERATION;
    protected int MAXIUMUM_POINTS = DEFAULT_MAXIMUM_POINTS;

    protected WorldState world;
    /* Rows => self, Cols => other teams */
    protected int[][] playerVisibility;
    protected int[][] foodVisibility;
    protected int[][] foodEatability;

    protected int[] teamAssignment;
    private int playerCounter = 0;
    private int foodCounter = 0;

    BarngaOnlineConfigsDefault(WorldState world) {
        this.world = world;
        this.playerVisibility = new int[TEAM_NUMBER][TEAM_NUMBER];
        this.foodVisibility = new int[TEAM_NUMBER][TEAM_NUMBER];

        initParams();
    }

    public void initParams() {
        world.setWorldSizeX(WORLD_X);
        world.setWorldSizeY(WORLD_Y);

        playerVisibility = DEFAULT_PLAYER_VISIBILITY;
        foodVisibility = DEFAULT_FOOD_VISIBILITY;
        foodEatability = DEFAULT_FOOD_EATABILITY;

        teamAssignment = new int[TEAM_NUMBER];
        for (int i = 0; i < TEAM_NUMBER; i++) {
            teamAssignment[i] = i;
        }
        // Note: Food will be generated per client connection
    }

    /**
     * Randomnly assign a new starting point for the player.
     */
    @Override
    public Coordinates initialCoordinates(long playerId, int teamId) {
        int x = (int)(Math.random() * WORLD_X);
        int y = (int)(Math.random() * WORLD_Y);
        Coordinates coord = new Coordinates(x, y);
        return coord;
    }

    @Override
    public int assignTeam(int playerId) {
        int index = playerCounter++ % teamAssignment.length;
        return teamAssignment[index];
    }

    @Override
    public boolean gameStarts() {
        Util.debug("We have %d players\n", world.getPlayers().size());
        return world.getPlayers().size() >= TEAM_NUMBER;
    }

    @Override
    public boolean gameEnds() {
        for (Points p : world.getPoints().values()) {
            if (MAXIUMUM_POINTS > 0 && p.getTotal() > MAXIUMUM_POINTS) {
                return true;
            }
        }

        // Finish game when all foods are taken
        boolean[] ateAll = new boolean[TEAM_NUMBER];
        for (int i = 0; i < ateAll.length; i++) {
            ateAll[i] = true;
        }

        for (Food f : world.getFoods().values()) {
            ateAll[f.team] = false;
        }

        boolean gameEnds = false;
        for (boolean b : ateAll) {
            gameEnds = gameEnds || b;
        }
        return gameEnds;
    }

    @Override
    public void onConnectCallback(Player player) {
        // Place food on world
        Util.debug("Generated food");

        int teamId = player.teamId;
        for (int i = 0; i < FOOD_PER_PLAYER; i++) {
            if (world.getFoods().size() > MAXIMUM_FOOD_PER_TEAM) {
                break;
            }

            int x = (int)(Math.random() * WORLD_X);
            int y = (int)(Math.random() * WORLD_Y);
            Coordinates coord = new Coordinates(x, y);
            Food food = new Food(foodCounter, teamId, coord, this);

            world.addFood(food);

            foodCounter++;
            Util.debug("Team %d: %s\n", teamId, food.coord.toString());
        }
    }

    @Override
    public void bumpPlayerCallback(Player bumper, Player bumpee) {
        Util.debug("Bump! Player");
    }

    @Override
    public void bumpFoodCallback(Player player, Food food) {
        Util.debug("Bump! Food");
    }

    @Override
    public void eatFoodCallback(Player player, Food food) {
        world.removeFood(food);

        Util.debug("Player %d of Team %d ate Food ID %d\n",
                player.id, player.teamId, food.id);
        int[] counts = new int[TEAM_NUMBER];
        for (Food f : world.getFoods().values()) {
            counts[f.team]++;
        }
        Util.debug("Remaining food");
        for (int i = 0; i < counts.length; i++) {
            Util.debug("%2d ", counts[i]);
        }
        Util.debug("");
    }

    @Override
    public HashSet<Food> generateFood(Player player, Food food) {
        HashSet<Food> ret = new HashSet<Food>();

        int teamId = player.teamId;
        for (int i = 0; i < FOOD_REGENERATION; i++) {
            if (world.getFoods().size() > MAXIMUM_FOOD_PER_TEAM) {
                break;
            }

            int x = (int)(Math.random() * WORLD_X);
            int y = (int)(Math.random() * WORLD_Y);
            Coordinates coord = new Coordinates(x, y);
            Food f = new Food(foodCounter++, teamId, coord, this);
            world.addFood(f);

            ret.add(f);
        }

        return ret;
    }

    @Override
    public boolean playerMovable(Player player, Coordinates newCoord) {
        if (player == null || newCoord == null) {
            return false;
        }

        // Staying inside the world
        boolean outOfBounds = newCoord.x >= WORLD_X || newCoord.x < 0
            || newCoord.y >= WORLD_Y || newCoord.y < 0;

        // Not bumping into anyone
        Player nearbyPlayer =
                world.visiblePlayerNear(player, newCoord, CLIENT_GRID_SIZE);
        boolean someoneAround = nearbyPlayer != null;

        // Not bumping into uneatable food
        Food visible =
                world.visibleFoodNear(player, newCoord, CLIENT_GRID_SIZE);
        Food eatable =
                world.eatableFoodNear(player, newCoord, CLIENT_GRID_SIZE);
        boolean nearbyUneatableFood = visible != null && eatable == null;

        return !outOfBounds && !someoneAround && !nearbyUneatableFood;
    }

    @Override
    public boolean foodVisible(Player player, Food food) {
        if (player == null || food == null) {
            return false;
        }

        return foodVisibility[player.teamId][food.team] != INVISIBLE;
    }

    @Override
    public boolean foodEatable(Player player, Food food) {
        if (player == null || food == null) {
            return false;
        }

        return foodEatability[player.teamId][food.team] == T;
    }

    @Override
    public void handlePoints(Player player, Food food) {
        int amount;
        if (food.team == player.teamId) {
            amount = OWN_TEAM_FOOD_POINTS;
        }
        else {
            amount = OTHER_TEAM_FOOD_POINTS;
        }

        Points teamPoints = world.getPoints().get(player.teamId);
        teamPoints.addPoints(player, amount);
    }

    public int[][] getPlayerVisibility() {
        return playerVisibility;
    }

    public int[][] getFoodVisibility() {
        return foodVisibility;
    }

    public int[][] getFoodEatability() {
        return foodEatability;
    }

    public WorldState getWorld() {
        return world;
    }

    public int getWorldSizeX() {
        return WORLD_X;
    }

    public int getWorldSizeY() {
        return WORLD_Y;
    }

    public int getGridSize() {
        return CLIENT_GRID_SIZE;
    }
}
