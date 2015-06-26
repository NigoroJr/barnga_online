package edu.miamioh.barnga_online;

import java.util.HashSet;

import edu.miamioh.barnga_online.events.MessagePlayerId;

/**
 *  Container class for player information.
 *
 * @author Naoki Mizuno
 */
public class Player {
    /* Accepted distance from food for eating in pixels */
    public static final int VALID_RANGE = 25;

    /* Hash code of the MessagePlayerName#playerName */
    public int id;
    public int teamId;
    /* Current coordinate */
    public Coordinates coord;

    protected BarngaOnlineConfigsDefault configs = null;

    public Player() {
    }

    public Player(int id, int teamId, Coordinates coord) {
        this.id = id;
        this.teamId = teamId;
        this.coord = coord;
    }

    public Player(int id, int teamId, Coordinates coord,
            BarngaOnlineConfigsDefault configs) {
        this(id, teamId, coord);
        this.configs = configs;
    }

    public Player(MessagePlayerId mes, BarngaOnlineConfigsDefault configs) {
        this(mes.id, mes.teamId, mes.coord, configs);
    }

    /* Copy constructor */
    public Player(Player other) {
        this.id = other.id;
        this.teamId = other.teamId;
        this.coord = new Coordinates(other.coord);
        this.configs = other.configs;
    }

    public boolean canSee(int teamId) {
        Team<Player> targetTeam = configs.getWorld().getTeam(teamId);
        return canSee(targetTeam);
    }

    public boolean canSee(Player other) {
        Team<Player> targetTeam = configs.getWorld().getTeam(other.teamId);
        return canSee(targetTeam);
    }

    public boolean canSee(Team<Player> team) {
        return getVisibleTeams().contains(team);
    }

    public boolean canSee(Food food) {
        Team<Player> targetTeam = configs.getWorld().getTeam(teamId);
        return food.seenBy().contains(targetTeam);
    }

    /**
     * Returns whether the player can eat the given food.
     *
     * This method does not check for the distance of the food.
     *
     * @param food
     */
	public boolean canEat(Food food) {
        boolean eatable = configs.foodEatable(this, food);
		return canSee(food) && eatable;
	}

    public int appearsTo(Player player) {
        return appearsTo(player.teamId);
    }

    public int appearsTo(Team<Player> team) {
        return appearsTo(team.getTeamId());
    }

    public int appearsTo(int teamId) {
        int self = teamId;
        int other = this.teamId;

        return configs.getPlayerVisibility()[self][other];
    }

    /**
     * Returns the set of teams that can see this player.
     *
     * Note that this is NOT the set of teams that this player can see.
     */
    public HashSet<Team<Player>> seenBy() {
        HashSet<Team<Player>> ret = new HashSet<Team<Player>>();

        int[][] playerVisibility = configs.getPlayerVisibility();
        for (int i = 0; i < playerVisibility.length; i++) {
            // Can see team (but may not appear as that team)
            if (playerVisibility[i][teamId] !=
                    BarngaOnlineConfigsDefault.INVISIBLE) {
                ret.add(configs.getWorld().getTeam(i));
            }
        }

        return ret;
    }

    /**
     * Returns the set of teams that this player can see.
     */
    public HashSet<Team<Player>> getVisibleTeams() {
        HashSet<Team<Player>> ret = new HashSet<Team<Player>>();

        int[][] playerVisibility = configs.getPlayerVisibility();
        for (int i = 0; i < playerVisibility.length; i++) {
            // Can see team (but may not appear as that team)
            if (playerVisibility[teamId][i] !=
                    BarngaOnlineConfigsDefault.INVISIBLE) {
                ret.add(configs.getWorld().getTeam(i));
            }
        }

        return ret;
    }

    public BarngaOnlineConfigsDefault getConfigs() {
        return configs;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Player)) {
			return false;
		}

		Player other = (Player)obj;
		return this.id == other.id;
	}
}
