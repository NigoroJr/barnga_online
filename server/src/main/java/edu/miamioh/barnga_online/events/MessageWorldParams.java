package edu.miamioh.barnga_online.events;

public class MessageWorldParams {
    public int worldSizeX;
    public int worldSizeY;
    public int gridSize;

    public MessageWorldParams() {
    }

    public MessageWorldParams(int worldSizeX, int worldSizeY, int gridSize) {
        this.worldSizeX = worldSizeX;
        this.worldSizeY = worldSizeY;
        this.gridSize = gridSize;
    }
}
