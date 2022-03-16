package com.github.lukebemish.simple_buffers.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import com.github.lukebemish.simple_buffers.blocks.ItemBufferBlock;
import com.github.lukebemish.simple_buffers.blocks.entities.ItemBufferBlockEntity;
import com.github.lukebemish.simple_buffers.forge.blocks.entities.CapableItemBufferBE;
import com.github.lukebemish.simple_buffers.util.RelativeSide;
import com.github.lukebemish.simple_buffers.util.ToggleState;

public class PlatformExtensionUtilImpl {
    public static ItemBufferBlockEntity getItemBufferBE(BlockPos blockPos, BlockState blockState) {
        return new CapableItemBufferBE(blockPos, blockState);
    }

    public static boolean bufferMoveItems(ItemBufferBlockEntity buffer, RelativeSide side, Level lvl) {
        boolean foundOne = false;

        Direction direction = RelativeSide.toDirection(side, buffer.getBlockState().getValue(ItemBufferBlock.FACING));
        Direction otherRelDir = direction.getOpposite();
        BlockPos lookPos = buffer.getBlockPos().relative(direction);

        BlockEntity be = lvl.getBlockEntity(lookPos);
        if (be != null) {
            LazyOptional<IItemHandler> lazyOtherCont = be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, otherRelDir);
            LazyOptional<IItemHandler> lazyBufferCont = buffer.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
            if (lazyBufferCont.isPresent() && lazyOtherCont.isPresent()) {
                //input
                IItemHandler otherCont = lazyOtherCont.resolve().get();
                IItemHandler bufferCont = lazyBufferCont.resolve().get();
                if (buffer.inputPullStates.getIOState(side) == ToggleState.ON && buffer.canTransferRedstone(side, true)) {
                    if (buffer.getBuffer().get(buffer.getBuffer().size() - 1).isEmpty()) {
                        for (int i = 0; i < otherCont.getSlots(); i++) {
                            ItemStack checkI = otherCont.extractItem(i, 1, true);
                            if (!checkI.isEmpty()) {
                                ItemStack checkSelf = bufferCont.insertItem(buffer.getContainerSize()-1, checkI, true);
                                if (checkSelf.isEmpty()) {
                                    ItemStack extracted = otherCont.extractItem(i, 1, false);
                                    bufferCont.insertItem(buffer.getContainerSize()-1, extracted, false);
                                    foundOne = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                //output
                if (buffer.outputPushStates.getIOState(side) == ToggleState.ON && buffer.canTransferRedstone(side, false)) {
                    if (!buffer.isEmpty()) {
                        searching:
                        for (int i = 0; i < buffer.getContainerSize(); i++) {
                            ItemStack checkI = bufferCont.extractItem(i, 1, true);
                            if (!checkI.isEmpty()) {
                                int maxAmount = buffer.outputLimit.getHeld(side);
                                int count = buffer.getExternalFilterCount(side, otherCont::getStackInSlot, otherCont.getSlots());
                                if (count < maxAmount) {
                                    for (int j = 0; j < otherCont.getSlots(); j++) {
                                        ItemStack checkOther = otherCont.insertItem(j, checkI, true);
                                        if (checkOther.isEmpty()) {
                                            ItemStack extracted = bufferCont.extractItem(i, 1, false);
                                            otherCont.insertItem(j, extracted, false);
                                            foundOne = true;
                                            break searching;
                                        }
                                    }
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
