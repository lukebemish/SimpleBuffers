package simplebuffers;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class SimpleBuffersItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(SimpleBuffers.MOD_ID, Registry.ITEM_REGISTRY);
    public static final RegistrySupplier<Item> ITEM_BUFFER_ITEM = ITEMS.register("item_buffer", () ->
            new BlockItem(SimpleBuffersBlocks.ITEM_BUFFER.get(), new Item.Properties().tab(SimpleBuffers.SIMPLE_BUFFERS_TAB)));
    public static final RegistrySupplier<Item> SPEED_UPGRADE_1 = ITEMS.register("speed_upgrade_1", () ->
            new Item(new Item.Properties().tab(SimpleBuffers.SIMPLE_BUFFERS_TAB)));
    public static final RegistrySupplier<Item> SPEED_UPGRADE_2 = ITEMS.register("speed_upgrade_2", () ->
            new Item(new Item.Properties().tab(SimpleBuffers.SIMPLE_BUFFERS_TAB)));
    public static final RegistrySupplier<Item> SPEC_SHEET = ITEMS.register("spec_sheet", () ->
            new Item(new Item.Properties().tab(SimpleBuffers.SIMPLE_BUFFERS_TAB).stacksTo(1)));
    public static final RegistrySupplier<Item> SMALL_SPEC_SHEET = ITEMS.register("small_spec_sheet", () ->
            new Item(new Item.Properties().tab(SimpleBuffers.SIMPLE_BUFFERS_TAB).stacksTo(1)));
    public static final RegistrySupplier<Item> SPEC_SHEET_STORED = ITEMS.register("spec_sheet_stored", () ->
            new Item(new Item.Properties()));
    public static final RegistrySupplier<Item> SMALL_SPEC_SHEET_STORED = ITEMS.register("small_spec_sheet_stored", () ->
            new Item(new Item.Properties()));

    public static void init() {
        ITEMS.register();
    }
}
