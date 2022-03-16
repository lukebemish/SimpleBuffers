package com.github.lukebemish.simple_buffers.util;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DirectedHolder<T> {
    public T north;
    public T south;
    public T east;
    public T west;
    public T up;
    public T down;

    public DirectedHolder() {
    }

    public static final ArrayList<Direction> DIRECTIONS = new ArrayList<Direction>();

    static {
        DIRECTIONS.add(Direction.NORTH);
        DIRECTIONS.add(Direction.SOUTH);
        DIRECTIONS.add(Direction.EAST);
        DIRECTIONS.add(Direction.WEST);
        DIRECTIONS.add(Direction.UP);
        DIRECTIONS.add(Direction.DOWN);
    }

    public T getHeld(@NotNull Direction dir) {
        switch (dir) {
            case NORTH:
                return this.north;
            case SOUTH:
                return this.south;
            case EAST:
                return this.east;
            case WEST:
                return this.west;
            case UP:
                return this.up;
            case DOWN:
                return this.down;
        }
        return this.east;
    }

    public void setHeld(@NotNull Direction dir, T state) {
        switch (dir) {
            case NORTH -> this.north = state;
            case SOUTH -> this.south = state;
            case EAST -> this.east = state;
            case WEST -> this.west = state;
            case UP -> this.up = state;
            case DOWN -> this.down = state;
        }
    }
}
