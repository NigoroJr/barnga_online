package edu.miamioh.culturecode;

import java.util.HashSet;

/**
 * Class that represents a team.
 *
 * A Team is a collection of Player.
 *
 * @author Naoki MIzuno
 */
@SuppressWarnings("hiding")
public class Team<Player> extends java.util.HashSet<Player> {
    /**
     * Generated serial version UID.
     */
    private static final long serialVersionUID = -7556585430178027176L;
    protected int teamId;
    protected CulturecodeConfigsDefault configs = null;

    public Team() {
        super();
    }

    public Team(int teamId) {
        super();
        this.teamId = teamId;
    }

    public Team(int teamId, CulturecodeConfigsDefault configs) {
        this(teamId);
        this.configs = configs;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public boolean canSee(Food food) {
        return food.seenBy().contains(this);
    }

    public boolean canSee(edu.miamioh.culturecode.Player player) {
        return canSee(player.teamId);
    }

    public boolean canSee(
            edu.miamioh.culturecode.Team<edu.miamioh.culturecode.Player> team) {
        return canSee(team.teamId);
    }

    public boolean canSee(int teamId) {
        int self = this.teamId;
        int other = teamId;
        return configs.getPlayerVisibility()[self][other] != CulturecodeConfigsDefault.INVISIBLE;
    }

    public int appearsTo(edu.miamioh.culturecode.Player player) {
        return appearsTo(player.teamId);
    }

    public int appearsTo(
            edu.miamioh.culturecode.Team<edu.miamioh.culturecode.Player> team) {
        return appearsTo(team.getTeamId());
    }

    public int appearsTo(int teamId) {
        int self = teamId;
        int other = this.teamId;

        return configs.getPlayerVisibility()[self][other];
    }

    public HashSet<Team<edu.miamioh.culturecode.Player>> seenBy() {
        HashSet<Team<edu.miamioh.culturecode.Player>> ret =
                new HashSet<edu.miamioh.culturecode.Team<edu.miamioh.culturecode.Player>>();

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

    public HashSet<edu.miamioh.culturecode.Team<edu.miamioh.culturecode.Player>> getVisibleTeams() {
        HashSet<Team<edu.miamioh.culturecode.Player>> ret =
                new HashSet<edu.miamioh.culturecode.Team<edu.miamioh.culturecode.Player>>();

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

    /**
     * Checks whether the given Object is the same as this team.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Team)) {
            return false;
        }

        @SuppressWarnings("unchecked")
        Team<Player> other = (Team<Player>)obj;
        return this.teamId == other.teamId;
    }
}
