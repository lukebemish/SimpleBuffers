package dev.lukebemish.simplebuffers

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class BufferBlockEntity extends BlockEntity {
    BufferBlockEntity(BlockEntityType<? extends BufferBlockEntity> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState)
    }
}
