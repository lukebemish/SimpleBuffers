package simplebuffers.util;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class SidedStateHolder<T extends SidedState> {
    public T left;
    public T right;
    public T front;
    public T back;
    public T up;
    public T down;
    public T def;
    public SidedStateHolder(T defVal) {
        left = defVal;
        right = defVal;
        front = defVal;
        back = defVal;
        up = defVal;
        down = defVal;
        def = defVal;
    }
    public void fromTag(CompoundTag tag) {
        left = (T) def.fromVal(tag.getInt("left"));
        right = (T) def.fromVal(tag.getInt("right"));
        front = (T) def.fromVal(tag.getInt("front"));
        back = (T) def.fromVal(tag.getInt("back"));
        up = (T) def.fromVal(tag.getInt("up"));
        down = (T) def.fromVal(tag.getInt("down"));
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("left",left.getVal());
        tag.putInt("right",right.getVal());
        tag.putInt("front",front.getVal());
        tag.putInt("back",back.getVal());
        tag.putInt("up",up.getVal());
        tag.putInt("down",down.getVal());

        return tag;
    }

    public T getIOState(@NotNull RelativeSide side) {
        switch (side) {
            case LEFT:
                return this.left;
            case RIGHT:
                return this.right;
            case FRONT:
                return this.front;
            case BACK:
                return this.back;
            case UP:
                return this.up;
            case DOWN:
                return this.down;
        }
        return this.front;
    }

    public void setIOState(@NotNull RelativeSide side, T state) {
        switch (side) {
            case LEFT -> this.left = state;
            case RIGHT -> this.right = state;
            case FRONT -> this.front = state;
            case BACK -> this.back = state;
            case UP -> this.up = state;
            case DOWN -> this.down = state;
        }
    }

    public void setIOState(@NotNull RelativeSide side, int state) {
        setIOState(side, (T) def.fromVal(state));
    }
}
