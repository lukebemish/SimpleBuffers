package com.github.lukebemish.simple_buffers;

import dev.architectury.registry.menu.MenuRegistry;
import com.github.lukebemish.simple_buffers.client.screen.ItemBufferScreen;

public class SimpleBuffersClient {
    public static void init() {
        MenuRegistry.registerScreenFactory(SimpleBuffersBlocks.ITEM_BUFFER_MENU.get(), ItemBufferScreen::new);
    }
}
