package dev.lukebemish.simplebuffers.transfer

import dev.lukebemish.simplebuffers.BufferState
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.jetbrains.annotations.Nullable

@CompileStatic
abstract class BufferTransfer {
    abstract BufferState getBufferState()

    Side[] sides = new Side[6]
    Side getSide(Direction direction) {
        return sides[direction.ordinal()]
    }

    int wouldInsert(BufferState state, TransferContext context, ItemStack stack) {
        if (!state.canInput()) {
            return 0
        }
        Side side = sides[context.side.ordinal()]
        return side.maxInput(stack, {
            return ItemStack.EMPTY
        }, context)
    }

    int wouldInsert(TransferContext context, ItemStack stack) {
        return wouldInsert(bufferState, context, stack)
    }

    int wouldExtract(BufferState state, TransferContext context, int realSlot, int amount) {
        Side side = sides[context.side.ordinal()]
        return Math.min(side.maxOutput(state.getStack(realSlot), {
            return ItemStack.EMPTY
        }, context), amount)
    }

    int wouldExtract(TransferContext context, int realSlot, int amount) {
        return wouldExtract(bufferState, context, realSlot, amount)
    }

    int realSlotCount() {
        return getBufferState().realSlotCount()
    }

    void inputStacks(Level level, BlockPos pos) {
        int offset = level.random.nextInt(6)
        for (int iRaw = 0; iRaw < 6; iRaw++) {
            if (!bufferState.canInput()) {
                continue
            }
            int i = iRaw + offset % 6
            Side side = sides[i]
            TransferContext context = new TransferContext(level, pos, Direction.values()[i])
            InputContext inputContext = inputContext(context)
            while (inputContext != null) {
                int toMove = side.maxInput(inputContext.stack, inputContext.simulated, context)
                if (toMove !== 0) {
                    int transfered = inputContext.stack.count - side.uncheckedInput(inputContext.stack.copyWithCount(toMove), inputContext.movement, context)
                    if (transfered !== 0) {
                        break
                    }
                }
                inputContext = inputContext.next.call()
            }
        }
    }

    int outputStack(ItemStack stack, Level level, BlockPos pos) {
        int remaining = stack.count
        int[] moving = new int[6]
        int[] requested = new int[6]
        int total = 0
        for (int i = 0; i < 6; i++) {
            Direction direction = Direction.values()[i]
            TransferContext context = new TransferContext(level, pos, direction)
            ItemMovement movement = makeSimulatedOutput(context)
            if (movement == null) {
                continue
            }
            int wanted = sides[i].maxOutput(stack, movement, context)
            moving[i] = wanted
            requested[i] = wanted
            total += wanted
        }
        if (remaining < total) {
            int totalToMove = 0
            IntList unfilled = new IntArrayList()
            for (int i = 0; i < 6; i++) {
                int split = (moving[i] * remaining).intdiv(total)
                moving[i] = split
                totalToMove += split
                if (requested[i] > split) {
                    unfilled.add(i)
                }
            }
            while (totalToMove < remaining) {
                int i = level.random.nextInt(unfilled.size())
                moving[i] += 1
                if (requested[i] <= moving[i]) {
                    unfilled.rem(i)
                }
                totalToMove += 1
            }
        }

        // Actually execute the move
        for (int i = 0; i < 6; i++) {
            if (moving[i] === 0) {
                continue
            }
            Direction direction = Direction.values()[i]
            TransferContext context = new TransferContext(level, pos, direction)
            ItemMovement movement = makeOutput(context)
            int leftover = sides[i].uncheckedOutput(stack.copyWithCount(moving[i]), movement, context)
            remaining -= leftover
        }
        return remaining
    }

    static @Nullable ItemMovement makeSimulatedOutput(TransferContext context) {
        //TODO
        return null
    }

    static @Nullable ItemMovement makeOutput(TransferContext context) {
        //TODO
        return null
    }

    static @Nullable InputContext inputContext(TransferContext context) {
        //TODO
        return null
    }

    @TupleConstructor
    static final class InputContext {
        ItemStack stack
        ItemMovement simulated
        ItemMovement movement
        Closure<InputContext> next
    }
}
