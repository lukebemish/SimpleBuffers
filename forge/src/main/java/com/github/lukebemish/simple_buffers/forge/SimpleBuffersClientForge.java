package com.github.lukebemish.simple_buffers.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import com.github.lukebemish.simple_buffers.SimpleBuffers;
import com.github.lukebemish.simple_buffers.SimpleBuffersClient;

@Mod.EventBusSubscriber(modid = SimpleBuffers.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SimpleBuffersClientForge {
    public static void init(final FMLClientSetupEvent event) {
        SimpleBuffersClient.init();
    }
}
