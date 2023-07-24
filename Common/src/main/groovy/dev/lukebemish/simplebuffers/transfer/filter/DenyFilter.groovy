package dev.lukebemish.simplebuffers.transfer.filter

import dev.lukebemish.simplebuffers.transfer.ItemMovement
import dev.lukebemish.simplebuffers.transfer.TransferContext
import groovy.transform.CompileStatic
import net.minecraft.world.item.ItemStack

@CompileStatic
class DenyFilter extends Filter {
    List<ItemStack> denylist

    @Override
    int maxTransfer(ItemStack stack, ItemMovement simulation, TransferContext context) {
        for (final disallow : denylist) {
            if (ItemStack.isSameItemSameTags(stack, disallow)) {
                return 0
            }
        }
        return stack.count - simulation.attemptTransfer(stack).count
    }

    @Override
    int transfer(ItemStack stack, ItemMovement movement, TransferContext context) {
        return movement.attemptTransfer(stack).count
    }
}
