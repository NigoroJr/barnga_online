package edu.miamioh.culturecode.listeners;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DisconnectListener;

import edu.miamioh.culturecode.Constants;
import edu.miamioh.culturecode.CulturecodeConfigs;
import edu.miamioh.culturecode.Player;
import edu.miamioh.culturecode.Team;
import edu.miamioh.culturecode.Util;
import edu.miamioh.culturecode.WorldState;
import edu.miamioh.culturecode.events.MessagePlayerId;

/**
 * Listener for EVENT_DISCONNECT events.
 *
 * @author Naoki Mizuno
 */
public class DisconnectEventListener implements DisconnectListener {
    protected WorldState world;
    protected CulturecodeConfigs configs;
    protected SocketIOServer server;

    public DisconnectEventListener(CulturecodeConfigs configs,
            WorldState world,
            SocketIOServer server) {
        super();

        this.world = world;
        this.configs = configs;
        this.server = server;
    }

    /**
     * Handles EVENT_DISCONNECT events.
     *
     * This method informs the other players about the disconnected player,
     * and checks whether there are enough players to continue the game or
     * not.
     */
    @Override
    public void onDisconnect(SocketIOClient client) {
        Player p = world.getClients().get(client);
        world.removePlayer(p);
        Util.debug("Player %d of Team %d has disconnected\n", p.id, p.teamId);

        // Inform other players about the disconnect
        for (Team t : world.getTeams().values()) {
            if (!t.canSee(p)) {
                continue;
            }

            MessagePlayerId mes = new MessagePlayerId(p);
            // Fake team ID just in case
            mes.teamId = p.appearsTo(t);

            String roomName = Integer.toString(t.getTeamId());
            BroadcastOperations room = server.getRoomOperations(roomName);
            room.sendEvent(Constants.EVENT_DISCONNECT, mes);
        }

        if (configs.gameEnds()) {
            server.getBroadcastOperations().sendEvent(Constants.EVENT_GAME_END);
            world.setGameStarted(false);
            Util.debug("Game has finished!");
        }
    }
}
