package simplebuffers.forge.blocks.entities;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import simplebuffers.blocks.ItemBufferBlock;
import simplebuffers.blocks.entities.ItemBufferBlockEntity;
import simplebuffers.util.ItemUtils;
import simplebuffers.util.RelativeSide;

import javax.annotation.Nonnull;

public class ItemBufferHandlerWrapper implements IItemHandler, IItemHandlerModifiable {
    private final ItemBufferBlockEntity be;
    private final Direction dir;

    public ItemBufferHandlerWrapper(ItemBufferBlockEntity be, Direction side) {
        this.be = be;
        this.dir = side;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        ItemStack newStack = stack.copy();
        newStack.setCount(1);
        return be.canPlaceItemThroughFace(slot, newStack, dir);
    }

    @Override
    public int getSlots() {
        return be.getContainerSize();
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int i) {
        RelativeSide side = RelativeSide.fromDirections(dir, be.getBlockState().getValue(ItemBufferBlock.FACING));
        switch (be.outputFilterStates.getIOState(side)) {
            case RR:
                ItemStack filter = be.getRROutItem(side);
                filter.setCount(1);
                ItemStack outItem = be.getItem(i).copy();
                if (ItemUtils.countlessMatches(filter,outItem)) {
                    outItem.setCount(Math.min(outItem.getCount(), be.getRROutRemaining(side)));
                    return outItem;
                }
            case BLACKLIST:
                if (be.checkBlacklist(side, be.getItem(i))) {
                    return be.getItem(i).copy();
                }
            case WHITELIST:
                if (be.checkWhitelist(side, be.getItem(i))) {
                    return be.getItem(i).copy();
                }
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (simulate) {
            return be.tryPlaceItem(slot, stack, dir);
        }
        ItemStack remainder = be.tryPlaceItem(slot, stack, dir);
        ItemStack toInsert = stack.copy();
        toInsert.setCount(stack.getCount()-remainder.getCount());
        if (!toInsert.isEmpty()) {
            be.setItem(slot, stack, this.dir);
        }
        return remainder;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int i, int j, boolean simulate) {
        ItemStack toTry = be.getItem(i).copy();
        toTry.setCount(j);
        ItemStack toTake = be.tryTakeItem(i, toTry, dir);
        if (!toTake.isEmpty()) {
            return be.removeItem(i, toTake.getCount(), simulate,dir);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int i) {
        return be.getMaxStackSize();
    }

    @Override
    public void setStackInSlot(int i, @NotNull ItemStack arg) {
        be.setItem(i, arg, this.dir);
    }
}
