package simplebuffers.forge;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import simplebuffers.SimpleBuffers;
import simplebuffers.SimpleBuffersNetworkingServer;

public class SimpleBuffersNetworkingClientImpl {
    public static void send(String id, SimpleBuffersNetworkingServer.BlockConfigUpdateMsg msg) {
        if (id == SimpleBuffers.SERVERBOUND_BUFFER_CONFIG_UPDATE_ID) {
            SimpleBuffersNetworkingServerImpl.SERVERBOUND_BUFFER_CONFIG_UPDATE.sendToServer(msg);
        }
    }
}
