package edu.miamioh.culturecode.events;

import edu.miamioh.culturecode.Coordinates;
import edu.miamioh.culturecode.Player;

/**
 * Container class for representing a player on the field.
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
