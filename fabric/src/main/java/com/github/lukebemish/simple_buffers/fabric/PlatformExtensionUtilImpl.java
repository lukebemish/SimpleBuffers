package com.github.lukebemish.simple_buffers.fabric;

import com.github.lukebemish.simple_buffers.blocks.ItemBufferBlock;
import com.github.lukebemish.simple_buffers.util.ToggleState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.github.lukebemish.simple_buffers.blocks.entities.ItemBufferBlockEntity;
import com.github.lukebemish.simple_buffers.util.RelativeSide;

public class PlatformExtensionUtilImpl {
    public static ItemBufferBlockEntity getItemBufferBE(BlockPos blockPos, BlockState blockState) {
        return new ItemBufferBlockEntity(blockPos,blockState);
    }

    public static boolean bufferMoveItems(ItemBufferBlockEntity buffer, RelativeSide side, Level lvl) {
        boolean foundOne = false;
        // find the direction, look for a Container there.
        Direction direction = RelativeSide.toDirection(side, buffer.getBlockState().getValue(ItemBufferBlock.FACING));
        Direction otherRelDir = direction.getOpposite();
        BlockPos lookPos = buffer.getBlockPos().relative(direction);
        Container otherCon = HopperBlockEntity.getContainerAt(lvl, lookPos);
        // do stuff w/ the container
        if (buffer.inputPullStates.getIOState(side) == ToggleState.ON && buffer.canTransferRedstone(side, true)) {
            if (buffer.getBuffer().get(buffer.getBuffer().size() - 1).isEmpty()) {
                if (otherCon != null) {
                    for (int i = 0; i < otherCon.getContainerSize(); i++) {
                        ItemStack checkI = otherCon.getItem(i).copy();
                        checkI.setCount(1);
                        if (buffer.canPlaceItemThroughFace(buffer.getContainerSize() - 1, checkI, direction)) {
                            ItemStack removed = otherCon.removeItem(i, 1);
                            if (!removed.isEmpty()) {
                                ItemStack remainder = HopperBlockEntity.addItem(otherCon, buffer, removed, direction);
                                if (remainder.isEmpty()) {
                                    foundOne = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (buffer.outputPushStates.getIOState(side) == ToggleState.ON && buffer.canTransferRedstone(side, false)) {
            if (!buffer.isEmpty()) {
                if (otherCon != null) {
                    for (int i = 0; i < buffer.getBuffer().size(); i++) {
                        ItemStack checkI = buffer.getBuffer().get(i).copy();
                        if (!checkI.isEmpty()) {
                            checkI.setCount(1);
                            int maxAmount = buffer.outputLimit.getHeld(side);
                            int count = buffer.getExternalFilterCount(side, otherCon::getItem, otherCon.getContainerSize());
                            if (count < maxAmount && buffer.canTakeItemThroughFace(i, checkI, direction)) {
                                ItemStack remainder = HopperBlockEntity.addItem(buffer, otherCon, checkI, otherRelDir);
                                if (remainder.isEmpty()) {
                                    ItemStack removed = buffer.removeItem(i, 1);
                                    foundOne = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return foundOne;
    }
}
