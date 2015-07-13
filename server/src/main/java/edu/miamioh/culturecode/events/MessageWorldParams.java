package edu.miamioh.culturecode.events;

/**
 * Class used for sending World information such as the world size to clients
 * via netty-socketio.
 *
 * @author Naoki Mizuno
 */
public class MessageWorldParams {
    public int worldSizeX;
    public int worldSizeY;
    public int gridSize;
    /**
     * Speed of the player.
     *
     * This is sent as a world parameter since it is sent at the
     * beginning and is common throughout the team.
     */
    public int playerSpeed;
    public int scoreVisible;

    public MessageWorldParams() {
    }

    public MessageWorldParams(int worldSizeX, int worldSizeY,
            int gridSize, int playerSpeed, int scoreVisible) {
        this.worldSizeX = worldSizeX;
        this.worldSizeY = worldSizeY;
        this.gridSize = gridSize;
        this.playerSpeed = playerSpeed;
        this.scoreVisible = scoreVisible;
    }
}
