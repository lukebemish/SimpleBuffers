package com.github.lukebemish.simple_buffers.fabric;

import com.github.lukebemish.simple_buffers.SimpleBuffersClient;
import net.fabricmc.api.ClientModInitializer;

public class SimpleBuffersClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SimpleBuffersClient.init();
    }
}
