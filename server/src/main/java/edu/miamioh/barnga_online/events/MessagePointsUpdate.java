package edu.miamioh.barnga_online.events;

import edu.miamioh.barnga_online.Player;

/**
 * Container class used when sending points update to client.
 *
 * @author Naoki Mizuno
 */
public class MessagePointsUpdate {
    public int teamId;
    /* The new TOTAL points for the team */
    public int newPoint;

    public MessagePointsUpdate() {
    }

    public MessagePointsUpdate(Player player, int newPoint) {
        this.teamId = player.teamId;
        this.newPoint = newPoint;
    }

    public MessagePointsUpdate(int teamId, int newPoint) {
        this.teamId = teamId;
        this.newPoint = newPoint;
    }
}
