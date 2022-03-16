package com.github.lukebemish.simple_buffers;

import com.github.lukebemish.simple_buffers.util.RelativeSide;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.github.lukebemish.simple_buffers.blocks.entities.ItemBufferBlockEntity;

public class PlatformExtensionUtil {
    @ExpectPlatform
    public static ItemBufferBlockEntity getItemBufferBE(BlockPos blockPos, BlockState blockState) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean bufferMoveItems(ItemBufferBlockEntity buffer, RelativeSide side, Level lvl) {
        throw new AssertionError();
    }
}