package com.github.lukebemish.simple_buffers.fabric;

import com.github.lukebemish.simple_buffers.SimpleBuffers;
import net.fabricmc.api.ModInitializer;

public class SimpleBuffersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SimpleBuffers.init();
    }
}
