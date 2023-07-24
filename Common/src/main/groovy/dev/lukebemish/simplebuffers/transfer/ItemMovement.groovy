package dev.lukebemish.simplebuffers.transfer

import net.minecraft.world.item.ItemStack

@FunctionalInterface
interface ItemMovement {
    ItemStack attemptTransfer(ItemStack stack)
}
