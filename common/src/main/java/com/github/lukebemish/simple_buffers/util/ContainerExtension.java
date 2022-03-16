package com.github.lukebemish.simple_buffers.util;

import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ContainerExtension {
    public static ItemStack removeItem(List<ItemStack> list, int i, int j, boolean simulate) {
        if (simulate) {
            if (i >= 0 && i < list.size() && !(list.get(i)).isEmpty() && j > 0) {
                ItemStack stack = list.get(i);
                int num = Math.min(j, stack.getCount());
                ItemStack stackOut = stack.copy();
                stackOut.setCount(num);
                return stackOut;
            }
            return ItemStack.EMPTY;
        }
        return ContainerHelper.removeItem(list, i, j);
    }
}
