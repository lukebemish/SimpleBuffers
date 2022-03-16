package com.github.lukebemish.simple_buffers;

import com.google.common.base.Suppliers;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class SimpleBuffers {
    public static final String MOD_ID = "simple_buffers";

    public static final String SERVERBOUND_BUFFER_CONFIG_UPDATE_ID = "simple_buffer_update";
    // We can use this if we don't want to use DeferredRegister
    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MOD_ID));
    // Registering a new creative tab
    public static final CreativeModeTab SIMPLE_BUFFERS_TAB = CreativeTabRegistry.create(new ResourceLocation(MOD_ID, "simple_buffers"), () ->
            new ItemStack(SimpleBuffersItems.ITEM_BUFFER_ITEM.get()));
    
    public static void init() {
        SimpleBuffersBlocks.init();
        SimpleBuffersItems.init();
        SimpleBuffersNetworkingServer.init();
    }
}
