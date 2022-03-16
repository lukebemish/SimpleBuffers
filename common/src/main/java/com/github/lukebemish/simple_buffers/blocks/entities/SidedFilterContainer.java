package com.github.lukebemish.simple_buffers.blocks.entities;

import com.github.lukebemish.simple_buffers.util.RelativeSide;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;

public class SidedFilterContainer implements Container {
    public NonNullList<ItemStack> buffer;
    private Container cont;
    private BlockEntity be;

    public <T extends BlockEntity & Container> SidedFilterContainer(T be) {
        this.buffer = NonNullList.withSize(9*2*6, ItemStack.EMPTY);
        this.be = be;
        this.cont = cont;
    }

    public void getSidedSublist(RelativeSide side, NonNullList<ItemStack> list) {
        int i = 0;
        for (ItemStack is : buffer.subList(RelativeSide.getListPlace(side) * 18, RelativeSide.getListPlace(side) * 18+18)) {
            list.set(i, is);
            i++;
        }
    }

    public void loadSidedSublist(RelativeSide side, NonNullList<ItemStack> list) {
        int i = RelativeSide.getListPlace(side) * 18;
        for (ItemStack is : list) {
            buffer.set(i, is);
            i++;
        }
    }

    public static int getIOSlotNum(boolean isInput, int slotPos, RelativeSide side) {
        int modifier1 = isInput ? 0 : 9;
        return RelativeSide.getListPlace(side) * 18 + slotPos + modifier1;
    }

    public ArrayList<ItemStack> getFilterList(boolean isInput, RelativeSide side) {
        ArrayList<ItemStack> output = new ArrayList<ItemStack>();
        for (int i = 0; i < 9; i++) {
            output.add(buffer.get(getIOSlotNum(isInput, i, side)));
        }
        return output;
    }

    @Override
    public int getContainerSize() {
        return 9*2*6;
    }

    @Override
    public boolean isEmpty() {
        return this.buffer.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int i) {
        return this.buffer.get(i);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        ItemStack itemStack = ContainerHelper.removeItem(this.buffer, i, j);
        if (!itemStack.isEmpty()) {
            this.setChanged();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ContainerHelper.takeItem(this.buffer, i);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        this.buffer.set(i, itemStack);
        if (itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    public void setChanged() {
        this.be.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.cont.stillValid(player);
    }

    @Override
    public void clearContent() {
        this.buffer.clear();
    }


}
