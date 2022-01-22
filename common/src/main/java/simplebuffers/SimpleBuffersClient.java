package simplebuffers;

import com.mojang.serialization.Decoder;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import simplebuffers.client.screen.ItemBufferScreen;

public class SimpleBuffersClient {
    public static void init() {
        MenuRegistry.registerScreenFactory(SimpleBuffersBlocks.ITEM_BUFFER_MENU.get(), ItemBufferScreen::new);
    }
}
