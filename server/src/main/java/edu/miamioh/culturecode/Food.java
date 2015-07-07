package edu.miamioh.culturecode;

import java.util.HashSet;

/**
 * Container class for representing foods on the field.
 *
 * @author Naoki Mizuno
 */
public class Food {
    public int id;
    /* What team it belongs to */
    public int team;
    /* Current coordinates */
    public Coordinates coord;

    protected CulturecodeConfigsDefault configs = null;

    public Food() {
    }

    public Food(int id, int team, Coordinates coord) {
        this.id = id;
        this.team = team;
        this.coord = coord;
    }

    /* Copy constructor */
    public Food(Food other) {
        this.id = other.id;
        this.team = other.team;
        this.coord = new Coordinates(other.coord);
        this.configs = other.configs;
    }

    public Food(int id, int team, Coordinates coord,
            CulturecodeConfigsDefault configs) {
        this(id, team, coord);
        this.configs = configs;
    }

    /**
     * Returns the set of teams that can see this food.
     */
    public HashSet<Team<Player>> seenBy() {
        HashSet<Team<Player>> ret = new HashSet<Team<Player>>();

        int[][] foodVisibility = configs.getFoodVisibility();
        for (int i = 0; i < foodVisibility.length; i++) {
            // Can see food (but may not appear as that team's food)
            if (foodVisibility[i][team] !=
                    CulturecodeConfigsDefault.INVISIBLE) {
                ret.add(configs.getWorld().getTeam(i));
            }
        }

        return ret;
    }

    public int appearsTo(Player player) {
        return appearsTo(player.teamId);
    }

    public int appearsTo(Team<Player> team) {
        return appearsTo(team.getTeamId());
    }

    public int appearsTo(int teamId) {
        int self = teamId;
        int other = team;

        return configs.getFoodVisibility()[self][other];
    }
}
