package edu.miamioh.culturecode.events;

import edu.miamioh.culturecode.Coordinates;
import edu.miamioh.culturecode.Player;

/**
 * Container class for telling a newly connected user its identity such as ID,
 * team, and the starting coordinates.
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
