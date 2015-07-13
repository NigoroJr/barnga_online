package edu.miamioh.culturecode;

import java.util.HashSet;

import edu.miamioh.culturecode.events.MessagePlayerId;

/**
 *  Container class for player information.
 *
 * @author Naoki Mizuno
 */
public class Player {
    /* Hash code of the MessagePlayerName#playerName */
    public int id;
    public int teamId;
    /* Current coordinate */
    public Coordinates coord;

    protected CulturecodeConfigsDefault configs = null;

    public Player() {
    }

    public Player(int id, int teamId, Coordinates coord) {
        this.id = id;
        this.teamId = teamId;
        this.coord = coord;
    }

    public Player(int id, int teamId, Coordinates coord,
            CulturecodeConfigsDefault configs) {
        this(id, teamId, coord);
        this.configs = configs;
    }

    public Player(MessagePlayerId mes, CulturecodeConfigsDefault configs) {
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
        Team targetTeam = configs.getWorld().getTeam(teamId);
        return canSee(targetTeam);
    }

    public boolean canSee(Player other) {
        Team targetTeam = configs.getWorld().getTeam(other.teamId);
        return canSee(targetTeam);
    }

    public boolean canSee(Team team) {
        return getVisibleTeams().contains(team);
    }

    public boolean canSee(Food food) {
        Team targetTeam = configs.getWorld().getTeam(teamId);
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

    /**
     * How this food looks to the given player.
     *
     * @param player the player looking at this player
     *
     * @return the team ID of the team that this player appears to belong to
     */
    public int appearsTo(Player player) {
        return appearsTo(player.teamId);
    }

    /**
     * How this food looks to the given team.
     *
     * @param team the team looking at this player
     *
     * @return the team ID of the team that this player appears to belong to
     */
    public int appearsTo(Team team) {
        return appearsTo(team.getTeamId());
    }

    /**
     * How this food looks to the given team.
     *
     * @param teamId the team looking at this player
     *
     * @return the team ID of the team that this player appears to belong to
     */
    public int appearsTo(int teamId) {
        int self = teamId;
        int other = this.teamId;

        return configs.getPlayerVisibility()[self][other];
    }

    /**
     * Returns the set of teams that can see this player.
     *
     * Note that this is NOT the set of teams that this player can see. For
     * that, use getVisibleTeams().
     *
     * @return HashSet of the teams that can see this player
     */
    public HashSet<Team> seenBy() {
        HashSet<Team> ret = new HashSet<Team>();

        int[][] playerVisibility = configs.getPlayerVisibility();
        for (int i = 0; i < playerVisibility.length; i++) {
            // Can see team (but may not appear as that team)
            if (playerVisibility[i][teamId] !=
                    CulturecodeConfigsDefault.INVISIBLE) {
                ret.add(configs.getWorld().getTeam(i));
            }
        }

        return ret;
    }

    /**
     * Returns the set of teams that this player can see.
     *
     * @return HashSet of the teams that this player can see
     */
    public HashSet<Team> getVisibleTeams() {
        HashSet<Team> ret = new HashSet<Team>();

        int[][] playerVisibility = configs.getPlayerVisibility();
        for (int i = 0; i < playerVisibility.length; i++) {
            // Can see team (but may not appear as that team)
            if (playerVisibility[teamId][i] !=
                    CulturecodeConfigsDefault.INVISIBLE) {
                ret.add(configs.getWorld().getTeam(i));
            }
        }

        return ret;
    }

    public CulturecodeConfigsDefault getConfigs() {
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
