package simplebuffers.fabric;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import simplebuffers.SimpleBuffers;
import simplebuffers.SimpleBuffersNetworkingServer;
import simplebuffers.blocks.entities.ItemBufferBlockEntity;

public class SimpleBuffersNetworkingServerImpl {
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(SimpleBuffers.MOD_ID, SimpleBuffers.SERVERBOUND_BUFFER_CONFIG_UPDATE_ID), (server, player, handler, buf, responseSender) -> {
            // Read packet data on the event loop
            SimpleBuffersNetworkingServer.BlockConfigUpdateMsg msg = SimpleBuffersNetworkingServer.BlockConfigUpdateMsg.decode(buf);

            server.execute(() -> {
                ServerLevel level = player.getLevel();
                if (level.hasChunkAt(msg.pos)) {
                    BlockEntity be = level.getBlockEntity(msg.pos);
                    if (be != null && be instanceof ItemBufferBlockEntity) {
                        for (int i = 0; i < msg.ints.length; i++) {
                            ((ItemBufferBlockEntity) be).dataAccess.set(i, msg.ints[i]);
                        }
                    }
                }
            });
        });
    }
}
