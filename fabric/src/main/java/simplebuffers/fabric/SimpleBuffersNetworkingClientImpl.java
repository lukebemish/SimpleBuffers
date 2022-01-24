package simplebuffers.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import simplebuffers.SimpleBuffers;
import simplebuffers.SimpleBuffersNetworkingServer;

public class SimpleBuffersNetworkingClientImpl {
    public static void send(String id, SimpleBuffersNetworkingServer.BlockConfigUpdateMsg msg) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        msg.encode(buf);
        ClientPlayNetworking.send(new ResourceLocation(SimpleBuffers.MOD_ID, id), buf);
    }
}
