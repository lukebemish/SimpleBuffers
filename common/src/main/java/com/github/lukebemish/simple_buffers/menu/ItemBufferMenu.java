package com.github.lukebemish.simple_buffers.menu;

import com.github.lukebemish.simple_buffers.SimpleBuffersBlocks;
import com.github.lukebemish.simple_buffers.util.RelativeSide;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import com.github.lukebemish.simple_buffers.blocks.entities.ItemBufferBlockEntity;
import com.github.lukebemish.simple_buffers.blocks.entities.SidedFilterContainer;

import java.util.ArrayList;

public class ItemBufferMenu extends AbstractContainerMenu {

    public Container container;
    public Container filterContainer;
    public ContainerData containerData;
    public ArrayList<ToggleableSlot> itemSlots = new ArrayList<ToggleableSlot>();
    public BlockPos pos;
    public int speedRank;
    public ArrayList<FilterSlot> filterSlots = new ArrayList<FilterSlot>();

    public ItemBufferMenu(int i, Inventory inventory, FriendlyByteBuf buf) {
        this(i, inventory, buf.readBlockPos(), new SimpleContainer(9), new SimpleContainer(9*2*6), new SimpleContainerData(ItemBufferBlockEntity.maxDataVal + 1), buf.readInt());
    }

    public void toggleCompressing() {
        int old = containerData.get(ItemBufferBlockEntity.maxDataVal);
        containerData.set(ItemBufferBlockEntity.maxDataVal, old==1 ? 0 : 1);
    }

    public void progressIOState(RelativeSide side) {
        int state = containerData.get(RelativeSide.ORDERED_SIDES.indexOf(side));
        int newState = 0;
        switch (state) {
            case 0 -> newState = 3;
            case 1 -> newState = 0;
            case 2 -> newState = 1;
            case 3 -> newState = 2;
        }
        containerData.set(RelativeSide.ORDERED_SIDES.indexOf(side), newState);
        this.broadcastChanges();
    }

    public void progressLimit(RelativeSide side, boolean isInput, boolean increase) {
        int offset = isInput ? 42 : 48;
        int initial = containerData.get(RelativeSide.ORDERED_SIDES.indexOf(side)+offset);
        initial += increase ? 1 : -1;
        if (initial > 256) {
            initial = 0;
        }
        if (initial < 0) {
            initial = 256;
        }
        containerData.set(RelativeSide.ORDERED_SIDES.indexOf(side)+offset, initial);
        this.broadcastChanges();
    }

    public void progressFilterState(RelativeSide side, boolean isInput) {
        int offset = isInput ? 6 : 12;
        int state = containerData.get(RelativeSide.ORDERED_SIDES.indexOf(side)+offset);
        int newState = 0;
        switch (state) {
            case 0 -> newState = 1;
            case 1 -> newState = 2;
            case 2 -> newState = 0;
        }
        containerData.set(RelativeSide.ORDERED_SIDES.indexOf(side)+offset, newState);
        this.broadcastChanges();
    }

    public void progressRedstoneState(RelativeSide side, boolean isInput) {
        int offset = isInput ? 30 : 36;
        int state = containerData.get(RelativeSide.ORDERED_SIDES.indexOf(side)+offset);
        int newState = 0;
        switch (state) {
            case 0 -> newState = 1;
            case 1 -> newState = 2;
            case 2 -> newState = 0;
        }
        containerData.set(RelativeSide.ORDERED_SIDES.indexOf(side)+offset, newState);
        this.broadcastChanges();
    }

    public void progressPushPullState(RelativeSide side, boolean isInput) {
        int offset = isInput ? 18 : 24;
        int state = containerData.get(RelativeSide.ORDERED_SIDES.indexOf(side)+offset);
        int newState = 0;
        if (state==0) newState = 1;
        containerData.set(RelativeSide.ORDERED_SIDES.indexOf(side)+offset, newState);
        this.broadcastChanges();
    }

    public ItemBufferMenu(int i, Inventory inventory, BlockPos pos, Container container, Container filterContainer, ContainerData data, int speedRank) {
        super(SimpleBuffersBlocks.ITEM_BUFFER_MENU.get(), i);
        this.speedRank = speedRank;
        this.pos = pos;
        int m;
        int l;
        this.container = container;
        this.containerData = data;
        this.filterContainer = filterContainer;
        checkContainerSize(container, 9);
        checkContainerSize(filterContainer, 9*2*6);

        //Our inventory
        for (m = 0; m < 9; ++m) {
            ToggleableSlot slot = new ToggleableSlot(container, m, 8 + m * 18, 17);
            this.addSlot(slot);
            itemSlots.add(slot);
        }
        //Filter slots
        for (RelativeSide side : RelativeSide.ORDERED_SIDES) {
            for (m = 0; m < 9; ++m) {
                int filterNum = SidedFilterContainer.getIOSlotNum(true, m, side);
                FilterSlot slot = new FilterSlot(filterContainer, filterNum, 8 + m * 18, 17);
                this.addSlot(slot);
                filterSlots.add(slot);
            }
            for (m = 0; m < 9; ++m) {
                int filterNum = SidedFilterContainer.getIOSlotNum(false, m, side);
                FilterSlot slot = new FilterSlot(filterContainer, filterNum, 8 + m * 18, 17+18+18);
                this.addSlot(slot);
                filterSlots.add(slot);
            }
        }
        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(inventory, m, 8 + m * 18, 142));
        }
        this.addDataSlots(containerData);
        this.addSlotListener(new ContainerListener() {
            public void slotChanged(AbstractContainerMenu abstractContainerMenu, int i, ItemStack itemStack) {
            }

            public void dataChanged(AbstractContainerMenu abstractContainerMenu, int i, int j) {
                ItemBufferMenu.this.broadcastChanges();
            }
        });
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    //TODO: fix this

    // Shift + Player Inv Slot
    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot < this.container.getContainerSize()) {
                if (!this.moveItemStackTo(originalStack, this.container.getContainerSize()+this.filterContainer.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (invSlot < this.container.getContainerSize() + this.filterContainer.getContainerSize()) {
                return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(originalStack, 0, this.container.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }
            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return newStack;
    }
}
