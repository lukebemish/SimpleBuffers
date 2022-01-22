package simplebuffers.util;

import net.minecraft.world.item.ItemStack;

public class ItemUtils {
    private static boolean countlessMatchInternal(ItemStack i1, ItemStack i2) {
        if (!i1.is(i2.getItem())) {
            return false;
        } else if (i1.getTag() == null && i2.getTag() != null) {
            return false;
        } else {
            return i1.getTag() == null || i1.getTag().equals(i2.getTag());
        }
    }

    public static boolean countlessMatches(ItemStack i1, ItemStack i2) {
        if (i1.isEmpty() && i2.isEmpty()) {
            return true;
        } else {
            return !i1.isEmpty() && !i2.isEmpty() && countlessMatchInternal(i1, i2);
        }
    }
}
