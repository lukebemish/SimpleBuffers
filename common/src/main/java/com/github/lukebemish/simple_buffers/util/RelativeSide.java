package com.github.lukebemish.simple_buffers.util;

import net.minecraft.core.Direction;

import java.util.ArrayList;

public enum RelativeSide {
    LEFT,
    RIGHT,
    FRONT,
    BACK,
    UP,
    DOWN;

    public static final ArrayList<RelativeSide> ORDERED_SIDES = new ArrayList<RelativeSide>();

    static {
        ORDERED_SIDES.add(LEFT);
        ORDERED_SIDES.add(RIGHT);
        ORDERED_SIDES.add(FRONT);
        ORDERED_SIDES.add(BACK);
        ORDERED_SIDES.add(UP);
        ORDERED_SIDES.add(DOWN);
    }

    public static int getListPlace(RelativeSide side) {
        switch(side) {
            case LEFT:
                return 0;
            case RIGHT:
                return 1;
            case FRONT:
                return 2;
            case BACK:
                return 3;
            case UP:
                return 4;
            case DOWN:
                return 5;
        }
        return 2;
    }

    public static RelativeSide fromDirections(Direction side, Direction facing) {
        if (side==Direction.UP) {
            return UP;
        }
        if (side==Direction.DOWN) {
            return DOWN;
        }
        if (side==facing) {
            return FRONT;
        }
        if (side==facing.getOpposite()) {
            return BACK;
        }
        if (side==facing.getClockWise()) {
            return LEFT;
        }
        if (side==facing.getClockWise().getOpposite()) {
            return RIGHT;
        }
        return FRONT;
    }

    public static Direction toDirection(RelativeSide side, Direction facing) {
        if (side==UP) {
            return Direction.UP;
        }
        if (side==DOWN) {
            return Direction.DOWN;
        }
        if (side==FRONT) {
            return facing;
        }
        if (side==BACK) {
            return facing.getOpposite();
        }
        if (side==LEFT) {
            return facing.getClockWise();
        }
        if (side==RIGHT) {
            return facing.getClockWise().getOpposite();
        }
        return facing;
    }
}
