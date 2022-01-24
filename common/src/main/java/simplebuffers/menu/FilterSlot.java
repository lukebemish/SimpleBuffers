package simplebuffers.menu;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class FilterSlot extends ToggleableSlot {
    private final int slot;

    public FilterSlot(Container container, int i, int j, int k) {
        super(container,i,j,k);
        this.slot = i;
    }

    @Override
    public ItemStack remove(int i) {
        this.container.removeItem(this.slot, i);
        return ItemStack.EMPTY;
    }

    private boolean vis = false;
    public boolean isVis() {
        return vis;
    }
    public void setVis(boolean vis) {
        this.vis = vis;
    }

    @Override
    public ItemStack safeInsert(ItemStack itemStack, int i) {
        ItemStack copyItemStack = itemStack.copy();
        if (!copyItemStack.isEmpty() && this.mayPlace(copyItemStack)) {
            ItemStack itemStack2 = this.getItem();
            int j = Math.min(Math.min(i, copyItemStack.getCount()), this.getMaxStackSize(copyItemStack) - itemStack2.getCount());
            if (itemStack2.isEmpty()) {
                this.set(copyItemStack.split(j));
            } else if (ItemStack.isSameItemSameTags(itemStack2, copyItemStack)) {
                copyItemStack.shrink(j);
                itemStack2.grow(j);
                this.set(itemStack2);
            }
        }
        return itemStack;
    }
}
