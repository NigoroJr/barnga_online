package edu.miamioh.culturecode;

import java.util.HashSet;

/**
 * Class that represents a team.
 *
 * A Team is a collection of Player.
 *
 * @author Naoki MIzuno
 */
public class Team {
    protected HashSet<Player> players;
    protected int teamId;
    protected CulturecodeConfigsDefault configs = null;

    public Team() {
    }

    public Team(int teamId) {
        this.teamId = teamId;
        this.players = new HashSet<Player>();
    }

    public Team(int teamId, CulturecodeConfigsDefault configs) {
        this(teamId);
        this.configs = configs;
    }

    public void add(Player p) {
        players.add(p);
    }

    public void remove(Player p) {
        players.remove(p);
    }

    public int size() {
        return players.size();
    }

    public HashSet<Player> players() {
        return players;
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

    public boolean canSee(Player player) {
        return canSee(player.teamId);
    }

    public boolean canSee(Team team) {
        return canSee(team.teamId);
    }

    public boolean canSee(int teamId) {
        int self = this.teamId;
        int other = teamId;
        return configs.getPlayerVisibility()[self][other]
                != CulturecodeConfigsDefault.INVISIBLE;
    }

    /**
     * How this team appears to the given player.
     *
     * @param player the player looking at this team
     *
     * @return the team ID of the team that this team appears to be
     */
    public int appearsTo(Player player) {
        return appearsTo(player.teamId);
    }

    /**
     * How this team appears to the given team.
     *
     * @param team the team looking at this team
     *
     * @return the team ID of the team that this team appears to be
     */
    public int appearsTo(Team team) {
        return appearsTo(team.getTeamId());
    }

    /**
     * How this team appears to the given team.
     *
     * @param teamId the team looking at this team
     *
     * @return the team ID of the team that this team appears to be
     */
    public int appearsTo(int teamId) {
        int self = teamId;
        int other = this.teamId;

        return configs.getPlayerVisibility()[self][other];
    }

    /**
     * Returns the set of teams that can see this team.
     *
     * Note that this is NOT the set of teams that this team can see. For
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
     * Returns the set of teams that this team can see.
     *
     * @return HashSet of the teams that this team can see
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

    /**
     * Checks whether the given Object is the same as this team.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Team)) {
            return false;
        }

        Team other = (Team)obj;
        return this.teamId == other.teamId;
    }
}
