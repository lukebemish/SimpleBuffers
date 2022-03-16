package com.github.lukebemish.simple_buffers.fabric;

import com.github.lukebemish.simple_buffers.SimpleBuffers;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import com.github.lukebemish.simple_buffers.SimpleBuffersNetworkingServer;

public class SimpleBuffersNetworkingClientImpl {
    public static void send(String id, SimpleBuffersNetworkingServer.BlockConfigUpdateMsg msg) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        msg.encode(buf);
        ClientPlayNetworking.send(new ResourceLocation(SimpleBuffers.MOD_ID, id), buf);
    }
}
