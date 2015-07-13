package edu.miamioh.culturecode.events;

import edu.miamioh.culturecode.Coordinates;
import edu.miamioh.culturecode.Player;

/**
 * Class used for sending Player identity such as ID, team, and starting
 * coordinates to clients via netty-socketio.
 *
 * @author Naoki Mizuno
 */
public class MessagePlayerId {
    public int id;
    public int teamId;
    public Coordinates coord;

    public MessagePlayerId() {
    }

    public MessagePlayerId(Player player) {
        this.id = player.id;
        this.teamId = player.teamId;
        this.coord = player.coord;
    }
}
