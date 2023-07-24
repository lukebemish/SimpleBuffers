package dev.lukebemish.simplebuffers.quilt

import dev.lukebemish.simplebuffers.BufferState
import dev.lukebemish.simplebuffers.transfer.BufferTransfer
import dev.lukebemish.simplebuffers.transfer.TransferContext
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import net.minecraft.world.item.ItemStack

import java.util.stream.IntStream

@CompileStatic
@TupleConstructor
@SuppressWarnings('UnstableApiUsage')
class BufferTransferWrapper extends SnapshotParticipant<BufferState> implements Storage<ItemVariant> {
    BufferTransfer bufferTransfer
    TransferContext context

    BufferState currentState = bufferTransfer.bufferState

    @Override
    long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        int maxInsert = (int) Math.min(maxAmount, resource.item.maxStackSize)
        ItemStack insertionStack = resource.toStack(maxInsert)
        int toInsert = bufferTransfer.wouldInsert(currentState, context, insertionStack)
        if (toInsert !== 0) {
            insertionStack.setCount(toInsert)
            currentState.insertUnsafe(insertionStack)
        }
        return toInsert
    }

    @Override
    long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        int maxExtract = (int) Math.min(maxAmount, resource.item.maxStackSize)
        for (int i = currentState.realSlotCount(); i >= 0; i++) {
            if (resource.matches(currentState.getStack(i))) {
                int wouldExtract = bufferTransfer.wouldExtract(currentState, context, i, maxExtract)
                if (wouldExtract !== 0) {
                    currentState.removeUnsafe(i, wouldExtract)
                    return wouldExtract
                }
            }
        }
        return 0
    }

    @Override
    Iterator<StorageView<ItemVariant>> iterator() {
        IntStream.range(0, this.currentState.realSlotCount()+1).<StorageView<ItemVariant>>mapToObj {
            return new WrapperStorageView(it)
        }.iterator()
    }

    @Override
    protected BufferState createSnapshot() {
        this.currentState = currentState.duplicate()
        return this.currentState
    }

    @Override
    protected void readSnapshot(BufferState snapshot) {
        this.currentState = snapshot
    }

    @TupleConstructor
    class WrapperStorageView implements StorageView<ItemVariant> {
        int idx

        @Override
        long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            int maxExtract = (int) Math.min(maxAmount, resource.item.maxStackSize)
            if (resource.matches(currentState.getStack(idx))) {
                int wouldExtract = bufferTransfer.wouldExtract(currentState, context, idx, maxExtract)
                if (wouldExtract !== 0) {
                    currentState.removeUnsafe(idx, wouldExtract)
                    return wouldExtract
                }
            }
            return 0
        }

        @Override
        boolean isResourceBlank() {
            return BufferTransferWrapper.this.currentState.getStack(idx).empty
        }

        @Override
        ItemVariant getResource() {
            return ItemVariant.of(BufferTransferWrapper.this.currentState.getStack(idx))
        }

        @Override
        long getAmount() {
            return BufferTransferWrapper.this.currentState.getStack(idx).count
        }

        @Override
        long getCapacity() {
            return 64
        }
    }
}
