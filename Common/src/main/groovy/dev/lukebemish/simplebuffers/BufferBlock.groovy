package dev.lukebemish.simplebuffers

import groovy.transform.CompileStatic
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

@CompileStatic
class BufferBlock<E extends BufferBlockEntity> extends Block implements EntityBlock {
    final BlockEntityType<E> blockEntityType

    BufferBlock(BlockEntityType<E> blockEntityType, Properties properties) {
        super(properties)
        this.blockEntityType = blockEntityType
    }

    @Override
    BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityType.create(pos, state)
    }

    @Override
    <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level outsideLevel, BlockState outsideState, BlockEntityType<T> blockEntityType) {
        return outsideLevel.clientSide ? null : wrapTicker(blockEntityType, { Level level, BlockPos blockPos, BlockState blockState, BufferBlockEntity blockEntity ->

        } as BlockEntityTicker<E>)
    }

    <T extends BlockEntity> BlockEntityTicker<T> wrapTicker(BlockEntityType<T> type, BlockEntityTicker<E> ticker) {
        if (type === this.blockEntityType) {
            return (BlockEntityTicker<T>) ticker
        }
        return null
    }
}
