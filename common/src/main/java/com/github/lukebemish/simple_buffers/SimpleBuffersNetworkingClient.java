package com.github.lukebemish.simple_buffers;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class SimpleBuffersNetworkingClient {
    @ExpectPlatform
    public static void send(String id, SimpleBuffersNetworkingServer.BlockConfigUpdateMsg msg) {
        throw new AssertionError();
    }
}
