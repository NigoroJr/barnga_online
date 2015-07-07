package edu.miamioh.culturecode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

import edu.miamioh.culturecode.events.MessagePlayerCoord;
import edu.miamioh.culturecode.listeners.ConnectEventListener;
import edu.miamioh.culturecode.listeners.DisconnectEventListener;
import edu.miamioh.culturecode.listeners.MoveListener;

/**
 * Framework for a browser game that simulates cultural privileges.
 *
 * TODO: Improve Javadoc
 *
 * @author Naoki Mizuno
 */
public class Main {
    protected static String hostName = Constants.HOSTNAME;
    protected static int port = Constants.PORT;
    protected static String rulesFileName = Constants.SAMPLE_RULES;

    public static void main(String[] args) {
        readConf(Constants.CONF_FILE);

        Configuration config = new Configuration();
        config.setHostname(hostName);
        config.setPort(port);

        SocketIOServer server = new SocketIOServer(config);
        server.start();
        Util.debug("Server started. Type " + Constants.SERVER_QUIT + " to quit.");

        // Game setup
        WorldState world = new WorldState();
        CulturecodeConfigsDefault ccConfigs =
            new CulturecodeConfigsFromFile(world, rulesFileName);

        // Event Listeners
        server.addConnectListener(
                new ConnectEventListener(ccConfigs, world, server));
        server.addDisconnectListener(
                new DisconnectEventListener(ccConfigs, world, server));
        server.addEventListener(Constants.EVENT_MOVE, MessagePlayerCoord.class,
                new MoveListener(ccConfigs, world, server));

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

    public static void readConf(String confFile) {
        Scanner scn;
        try {
            scn = new Scanner(new File(confFile));
        }
        catch (FileNotFoundException e) {
            System.err.println("Could not find rules file");
            System.err.println("Using default configuration values");
            return;
        }

        while (scn.hasNextLine()) {
            String line = scn.nextLine();
            String[] tokens = line.split(":", 2);

            // Strip off whitespaces
            String k = tokens[0].trim();
            String v = tokens[1].trim();

            if (k.equals(Constants.CONF_HOSTNAME)) {
                hostName = v;
            }
            else if (k.equals(Constants.CONF_PORT)) {
                port = Integer.parseInt(v);
            }
            else if (k.equals(Constants.CONF_RULES)) {
                rulesFileName = v;
            }
        }

        scn.close();
    }
}
