package edu.miamioh.barnga_online;

import java.util.Scanner;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

import edu.miamioh.barnga_online.events.MessagePlayerCoord;
import edu.miamioh.barnga_online.listeners.ConnectEventListener;
import edu.miamioh.barnga_online.listeners.DisconnectEventListener;
import edu.miamioh.barnga_online.listeners.MoveListener;

/**
 * Framework for a browser game that's similar to the game barnga.
 *
 * TODO: Improve Javadoc
 *
 * @author Naoki Mizuno
 */
public class Main {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.setHostname(Constants.HOSTNAME);
        config.setPort(Constants.PORT);

        SocketIOServer server = new SocketIOServer(config);
        server.start();
        Util.debug("Server started. Type " + Constants.SERVER_QUIT + " to quit.");

        // Game setup
        WorldState world = new WorldState();
        BarngaOnlineConfigsDefault barngaConfigs =
            new BarngaOnlineConfigsFromFile(world);

        // Event Listeners
        server.addConnectListener(
                new ConnectEventListener(barngaConfigs, world, server));
        server.addDisconnectListener(
                new DisconnectEventListener(barngaConfigs, world, server));
        server.addEventListener(Constants.EVENT_MOVE, MessagePlayerCoord.class,
                new MoveListener(barngaConfigs, world, server));

        Scanner scn = new Scanner(System.in);
        while (true) {
            String input = scn.nextLine();
            if (input.equals(Constants.SERVER_QUIT)) {
                break;
            }
        }
        scn.close();

        Util.debug("Stopping Server");
        server.stop();
        Util.debug("Server has stopped");
    }
}
