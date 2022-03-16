package com.github.lukebemish.simple_buffers.util;

public enum RedstoneState implements SidedState {
    DISABLED(0),
    HIGH(1),
    LOW(2);

    private final int state;

    private RedstoneState(int state) {
        this.state = state;
    }

    public int getVal() {
        return this.state;
    }

    public static RedstoneState fromValStatic(int state) {
        switch (state) {
            case 0:
                return DISABLED;
            case 1:
                return HIGH;
            case 2:
                return LOW;
        }
        return DISABLED;
    }

    public RedstoneState fromVal(int state) {
        return RedstoneState.fromValStatic(state);
    }
}
