package simplebuffers.util;

import org.jetbrains.annotations.NotNull;

public class SidedHolder<T> {
    public T left;
    public T right;
    public T front;
    public T back;
    public T up;
    public T down;

    public SidedHolder() {
    }

    public T getHeld(@NotNull RelativeSide side) {
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

    public void setHeld(@NotNull RelativeSide side, T state) {
        switch (side) {
            case LEFT -> this.left = state;
            case RIGHT -> this.right = state;
            case FRONT -> this.front = state;
            case BACK -> this.back = state;
            case UP -> this.up = state;
            case DOWN -> this.down = state;
        }
    }
}
