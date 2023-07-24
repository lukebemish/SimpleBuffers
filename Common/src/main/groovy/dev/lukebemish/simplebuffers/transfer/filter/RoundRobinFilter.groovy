package dev.lukebemish.simplebuffers.transfer.filter

import dev.lukebemish.simplebuffers.transfer.ItemMovement
import dev.lukebemish.simplebuffers.transfer.TransferContext
import net.minecraft.world.item.ItemStack

class RoundRobinFilter extends Filter {
    List<ItemStack> roundrobin
    int index
    int remaining

    @Override
    int maxTransfer(ItemStack stack, ItemMovement simulation, TransferContext context) {
        if (ItemStack.isSameItemSameTags(stack, roundrobin[index])) {
            return stack.count - simulation.attemptTransfer(stack.copyWithCount(Math.min(stack.count, remaining))).count
        }
        return 0
    }

    @Override
    int transfer(ItemStack stack, ItemMovement movement, TransferContext context) {
        int moved = stack.count - movement.attemptTransfer(stack.copyWithCount(Math.min(stack.count, remaining))).count
        remaining -= moved
        if (remaining == 0) {
            index = (index + 1) % roundrobin.size()
            remaining = roundrobin[index].count
        }
        return moved
    }
}
