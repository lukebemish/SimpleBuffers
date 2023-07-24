package dev.lukebemish.simplebuffers.forge

import dev.lukebemish.simplebuffers.transfer.BufferTransfer
import dev.lukebemish.simplebuffers.transfer.TransferContext
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.IItemHandler
import org.jetbrains.annotations.NotNull

@TupleConstructor
@CompileStatic
class BufferTransferWrapper implements IItemHandler {
    BufferTransfer bufferTransfer
    TransferContext context

    @Override
    int getSlots() {
        return bufferTransfer.realSlotCount() + 1
    }

    @Override
    ItemStack getStackInSlot(int fakeSlot) {
        if (fakeSlot === 0) {
            return ItemStack.EMPTY
        }
        return bufferTransfer.bufferState.getStack(fakeSlot - 1)
    }

    @Override
    ItemStack insertItem(int fakeSlot, @NotNull ItemStack stack, boolean simulate) {
        if (fakeSlot !== 0) {
            return stack
        }
        int couldInsert = bufferTransfer.wouldInsert(context, stack)
        if (!simulate) {
            bufferTransfer.bufferState.insertUnsafe(stack.copyWithCount(couldInsert))
            if (stack.count === couldInsert) {
                return ItemStack.EMPTY
            }
        }
        return stack.copyWithCount(stack.count - couldInsert)
    }

    @Override
    ItemStack extractItem(int fakeSlot, int amount, boolean simulate) {
        if (fakeSlot !== 0) {
            return ItemStack.EMPTY
        }
        int realSlot = fakeSlot - 1
        int couldExtract = bufferTransfer.wouldExtract(context, realSlot, amount)
        if (couldExtract === 0) {
            return ItemStack.EMPTY
        }
        ItemStack output = bufferTransfer.bufferState.getStack(realSlot).copyWithCount(couldExtract)
        if (!simulate) {
            bufferTransfer.bufferState.removeUnsafe(realSlot, couldExtract)
        }
        return output
    }

    @Override
    int getSlotLimit(int slot) {
        return 64
    }

    @Override
    boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true
    }
}
