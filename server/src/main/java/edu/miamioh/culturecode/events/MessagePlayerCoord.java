package edu.miamioh.culturecode.events;

import edu.miamioh.culturecode.Coordinates;
import edu.miamioh.culturecode.Player;

/**
 * Class used for sending Player information to clients via netty-socketio.
 *
 * @author Naoki Mizuno
 */
public class MessagePlayerCoord {
    public MessagePlayerId player;
    public Coordinates newCoord;

    public MessagePlayerCoord() {
    }

    public MessagePlayerCoord(Player player, Coordinates newCoord) {
        this.player = new MessagePlayerId(player);
        this.newCoord = newCoord;
    }
}
