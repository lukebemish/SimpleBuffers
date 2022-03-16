package com.github.lukebemish.simple_buffers;

import com.github.lukebemish.simple_buffers.blocks.ItemBufferBlock;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import com.github.lukebemish.simple_buffers.blocks.entities.ItemBufferBlockEntity;
import com.github.lukebemish.simple_buffers.menu.ItemBufferMenu;

public class SimpleBuffersBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(SimpleBuffers.MOD_ID, Registry.BLOCK_REGISTRY);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(SimpleBuffers.MOD_ID, Registry.BLOCK_ENTITY_TYPE_REGISTRY);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(SimpleBuffers.MOD_ID, Registry.MENU_REGISTRY);

    public static final RegistrySupplier<Block> ITEM_BUFFER = BLOCKS.register("item_buffer", () ->
            new ItemBufferBlock(Block.Properties.of(Material.METAL).strength(1.5f)));

    public static final RegistrySupplier<BlockEntityType<ItemBufferBlockEntity>> ITEM_BUFFER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("item_buffer", () -> BlockEntityType.Builder.of(PlatformExtensionUtil::getItemBufferBE, ITEM_BUFFER.get()).build(null));

    public static final RegistrySupplier<MenuType<ItemBufferMenu>> ITEM_BUFFER_MENU =
            MENU_TYPES.register("item_buffer", () -> MenuRegistry.ofExtended(ItemBufferMenu::new));

    public static void init() {
        BLOCKS.register();
        BLOCK_ENTITIES.register();
        MENU_TYPES.register();
    }
}
