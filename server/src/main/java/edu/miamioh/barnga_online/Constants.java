package edu.miamioh.barnga_online;

/**
 * @author Naoki Mizuno
 */
public class Constants {
    public static final boolean DEBUG = true;

    public static final String SERVER_QUIT = "q";

    public static final String HOSTNAME = "10.33.50.149";
    public static final int PORT = 3000;

    /* Events sent FROM the server */
    public static final String EVENT_PLAYER_ID = "playerId";
    public static final String EVENT_DISCONNECT = "disconnect";
    public static final String EVENT_GAME_START = "gameStart";
    public static final String EVENT_GAME_END = "gameEnd";
    public static final String EVENT_PLAYER_UPDATE = "playerUpdate";
    public static final String EVENT_FOOD_UPDATE = "foodUpdate";
    public static final String EVENT_POINTS_UPDATE = "pointsUpdate";

    /* Events sent TO the server */
    public static final String EVENT_MOVE = "move";
    // Note: Following events are optional for the prototype
    public static final String EVENT_TEAM_SELECTED = "teamSelected";
}
