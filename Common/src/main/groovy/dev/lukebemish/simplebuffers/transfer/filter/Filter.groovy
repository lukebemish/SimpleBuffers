package dev.lukebemish.simplebuffers.transfer.filter

import dev.lukebemish.simplebuffers.transfer.ItemMovement
import dev.lukebemish.simplebuffers.transfer.TransferContext
import groovy.transform.CompileStatic
import net.minecraft.world.item.ItemStack

@CompileStatic
abstract class Filter {
    abstract int maxTransfer(ItemStack stack, ItemMovement simulation, TransferContext context)
    abstract int transfer(ItemStack stack, ItemMovement movement, TransferContext context)
}
