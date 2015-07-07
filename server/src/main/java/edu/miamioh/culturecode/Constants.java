package edu.miamioh.culturecode;

/**
 * @author Naoki Mizuno
 */
public class Constants {
    public static final boolean DEBUG = true;

    public static final String SERVER_QUIT = "q";

    public static final String CONF_FILE = "culturecode.conf";

    /* Keys for the conf file */
    public static final String CONF_HOSTNAME = "server_host";
    public static final String CONF_PORT = "server_port";
    public static final String CONF_RULES = "rules";

    public static final String HOSTNAME = "localhost";
    public static final int PORT = 3000;
    public static final String SAMPLE_RULES = "sample.toml";

    /* Events sent FROM the server */
    public static final String EVENT_PLAYER_ID = "playerId";
    public static final String EVENT_WORLD_PARAMS = "worldParams";
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
