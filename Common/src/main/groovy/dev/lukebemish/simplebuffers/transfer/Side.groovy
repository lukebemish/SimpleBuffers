package dev.lukebemish.simplebuffers.transfer

import dev.lukebemish.simplebuffers.transfer.filter.Filter
import groovy.transform.CompileStatic
import net.minecraft.world.item.ItemStack

@CompileStatic
class Side {
    Filter outFilter
    Filter inFilter
    boolean output
    boolean input
    RedstoneBehavior redstoneBehavior

    private boolean isEnabled(TransferContext context) {
        return redstoneBehavior.test(context)
    }

    int maxOutput(ItemStack stack, ItemMovement simulation, TransferContext context) {
        if (!output && !isEnabled(context)) {
            return 0
        }
        return outFilter.maxTransfer(stack, simulation, context)
    }

    int uncheckedOutput(ItemStack stack, ItemMovement movement, TransferContext context) {
        if (!output && !isEnabled(context)) {
            return stack.count
        }
        return outFilter.transfer(stack, movement, context)
    }

    int maxInput(ItemStack stack, ItemMovement simulation, TransferContext context) {
        if (!input && !isEnabled(context)) {
            return 0
        }
        return inFilter.maxTransfer(stack, simulation, context)
    }

    int uncheckedInput(ItemStack stack, ItemMovement movement, TransferContext context) {
        if (!input && !isEnabled(context)) {
            return stack.count
        }
        return inFilter.transfer(stack, movement, context)
    }
}
