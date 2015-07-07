package edu.miamioh.culturecode.events;

public class MessageWorldParams {
    public int worldSizeX;
    public int worldSizeY;
    public int gridSize;
    public int playerSpeed;

    public MessageWorldParams() {
    }

    public MessageWorldParams(int worldSizeX, int worldSizeY,
            int gridSize, int playerSpeed) {
        this.worldSizeX = worldSizeX;
        this.worldSizeY = worldSizeY;
        this.gridSize = gridSize;
        this.playerSpeed = playerSpeed;
    }
}
