package simplebuffers;

import dev.architectury.registry.menu.MenuRegistry;
import simplebuffers.client.screen.ItemBufferScreen;

public class SimpleBuffersClient {
    public static void init() {
        MenuRegistry.registerScreenFactory(SimpleBuffersBlocks.ITEM_BUFFER_MENU.get(), ItemBufferScreen::new);
    }
}
