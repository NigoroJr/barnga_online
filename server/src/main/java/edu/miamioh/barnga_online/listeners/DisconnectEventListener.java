package edu.miamioh.barnga_online.listeners;

import edu.miamioh.barnga_online.*;
import edu.miamioh.barnga_online.events.*;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.*;

/**
 * Listeners for when receiving the EVENT_DISCONNECT event.
 *
 * @author Naoki Mizuno
 */
public class DisconnectEventListener implements DisconnectListener {
    protected WorldState world;
    protected BarngaOnlineConfigs configs;
    protected SocketIOServer server;

    public DisconnectEventListener(BarngaOnlineConfigs configs, WorldState world,
            SocketIOServer server) {
        super();

        this.world = world;
        this.configs = configs;
        this.server = server;
    }

    @Override
    public void onDisconnect(SocketIOClient client) {
        Player p = world.getClients().get(client.getRemoteAddress());
        world.removePlayer(p);
        Util.debug("Player %d of Team %d has disconnected\n", p.id, p.teamId);

        // Inform other players about the disconnect
        for (Team<Player> t : world.getTeams().values()) {
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
