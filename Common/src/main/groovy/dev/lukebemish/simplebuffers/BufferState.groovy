package dev.lukebemish.simplebuffers

import net.minecraft.world.item.ItemStack

interface BufferState {
    List<ItemStack> getMenuStacks()
    int realSlotCount()
    ItemStack getStack(int realLocation)
    boolean canInput()
    void insertUnsafe(ItemStack stack)
    void removeUnsafe(int actualSlot, int count)

    BufferState duplicate()
}
