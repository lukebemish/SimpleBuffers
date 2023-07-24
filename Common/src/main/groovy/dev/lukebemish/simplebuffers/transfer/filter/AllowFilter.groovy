package dev.lukebemish.simplebuffers.transfer.filter

import dev.lukebemish.simplebuffers.transfer.ItemMovement
import dev.lukebemish.simplebuffers.transfer.TransferContext
import net.minecraft.world.item.ItemStack

class AllowFilter extends Filter {
    List<ItemStack> allowlist

    @Override
    int maxTransfer(ItemStack stack, ItemMovement simulation, TransferContext context) {
        for (final disallow : allowlist) {
            if (ItemStack.isSameItemSameTags(stack, disallow)) {
                return stack.count - simulation.attemptTransfer(stack).count
            }
        }
        return 0
    }

    @Override
    int transfer(ItemStack stack, ItemMovement movement, TransferContext context) {
        return movement.attemptTransfer(stack).count
    }
}
