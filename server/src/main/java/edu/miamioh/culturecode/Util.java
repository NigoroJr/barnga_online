package edu.miamioh.culturecode;

import edu.miamioh.culturecode.events.MessagePlayerCoord;
import edu.miamioh.culturecode.events.MessageWorldParams;

/**
 * A bunch of utility methods.
 *
 * @author Naoki Mizuno
 */

public class Util {
    protected WorldState world;
    protected CulturecodeConfigsDefault configs;

    /**
     * Prints out the given string if Constants.DEBUG is true.
     *
     * @param str String to print out
     */
    public static void debug(String str) {
        if (Constants.DEBUG) {
            System.out.println(str);
        }
    }

    /**
     * Prints out the given string in the also given format if Constants.DEBUG
     * is true.
     *
     * The format is the same as printf.
     *
     * @param fmt the format of the string
     *
     * @param args the arguments
     */
    public static void debug(String fmt, Object... args) {
        if (Constants.DEBUG) {
            System.out.printf(fmt, args);
        }
    }

    public static double distance(Coordinates a, Coordinates b) {
		int x_diff = Math.abs(a.x - b.x);
		int y_diff = Math.abs(a.y - b.y);

		return Math.sqrt(Math.pow(x_diff, 2) + Math.pow(y_diff, 2));
    }

    public Util(WorldState world, CulturecodeConfigsDefault configs) {
        this.world = world;
        this.configs = configs;
    }

    /**
     * Creates a fake message with the team ID that could be different from
     * the actual team ID.
     *
     * @param team the team that will be getting the message
     *
     * @param player the player that the message is about
     */
    public MessagePlayerCoord makeFakePlayerMessage(Team<Player> team,
            Player player, Coordinates newCoord) {
        return makeFakePlayerMessage(team.getTeamId(), player, newCoord);
    }

    /**
     * Creates a fake message for `one' where the team ID may be different
     * from the actual team that `two' belongs to.
     *
     * @param one the player that will be getting the message
     *
     * @param two the player that the message is about
     */
    public MessagePlayerCoord makeFakePlayerMessage(Player one,
            Player two, Coordinates newCoord) {
        return makeFakePlayerMessage(one.teamId, two, newCoord);
    }

    /**
     * Creates a fake message from teamId to `player'.
     *
     * teamId is the team who will be getting the message.
     */
    public MessagePlayerCoord makeFakePlayerMessage(int teamId,
            Player player, Coordinates newCoord) {
        Player fakePlayer = new Player(player);

        fakePlayer.teamId = fakePlayer.appearsTo(teamId);

        MessagePlayerCoord mes =
            new MessagePlayerCoord(fakePlayer, new Coordinates(newCoord));

        return mes;
    }

    public MessageWorldParams makeWorldParamsMessage(int teamId) {
      MessageWorldParams mes = new MessageWorldParams(
          configs.getWorldSizeX(),
          configs.getWorldSizeY(),
          configs.getGridSize(),
          configs.getSpeed(teamId),
          configs.getScoreVisibility(teamId));
      return mes;
    }
}
