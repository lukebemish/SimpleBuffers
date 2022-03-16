package com.github.lukebemish.simple_buffers.forge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import com.github.lukebemish.simple_buffers.SimpleBuffers;
import com.github.lukebemish.simple_buffers.SimpleBuffersNetworkingServer;
import com.github.lukebemish.simple_buffers.blocks.entities.ItemBufferBlockEntity;

import java.util.function.Supplier;

public class SimpleBuffersNetworkingServerImpl {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel SERVERBOUND_BUFFER_CONFIG_UPDATE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SimpleBuffers.MOD_ID, SimpleBuffers.SERVERBOUND_BUFFER_CONFIG_UPDATE_ID),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        SERVERBOUND_BUFFER_CONFIG_UPDATE.registerMessage(1, SimpleBuffersNetworkingServer.BlockConfigUpdateMsg.class,
                SimpleBuffersNetworkingServer.BlockConfigUpdateMsg::encode,
                SimpleBuffersNetworkingServer.BlockConfigUpdateMsg::decode,
                SimpleBuffersNetworkingServerImpl::consumeBufferConfigUpdate);
    }

    public static void consumeBufferConfigUpdate(SimpleBuffersNetworkingServer.BlockConfigUpdateMsg msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
                if (ctx.get().getSender().getLevel().hasChunkAt(msg.pos)) {
                    ServerLevel level = ctx.get().getSender().getLevel();
                    BlockEntity be = level.getBlockEntity(msg.pos);
                    if (be != null && be instanceof ItemBufferBlockEntity) {
                        for (int i = 0; i < msg.ints.length; i++) {
                            ((ItemBufferBlockEntity) be).dataAccess.set(i, msg.ints[i]);
                        }
                    }
                }
        });
        ctx.get().setPacketHandled(true);
    }
}
