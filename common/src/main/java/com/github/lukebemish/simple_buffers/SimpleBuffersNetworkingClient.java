package com.github.lukebemish.simple_buffers;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SimpleBuffersNetworkingClient {
    @ExpectPlatform
    public static void send(String id, SimpleBuffersNetworkingServer.BlockConfigUpdateMsg msg) {
        throw new AssertionError();
    }
}
