package edu.miamioh.culturecode.events;

import edu.miamioh.culturecode.Coordinates;
import edu.miamioh.culturecode.Food;

/**
 * Class used for sending Food information to clients via netty-socketio.
 *
 * @author Naoki Mizuno
 */
public class MessageFoodCoord {
    public Food food;
    public Coordinates newCoord;
    public boolean gone;

    public MessageFoodCoord() {
    }

    public MessageFoodCoord(Food food, Coordinates newCoord, boolean gone) {
        this.food = food;
        this.newCoord = newCoord;
        this.gone = gone;
    }
}
