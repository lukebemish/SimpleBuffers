package com.github.lukebemish.simple_buffers.util;

public enum ToggleState implements SidedState {
    ON(0),
    OFF(1);

    private final int state;

    private ToggleState(int state) {
        this.state = state;
    }

    public int getVal() {
        return this.state;
    }

    public static ToggleState fromValStatic(int state) {
        switch(state) {
            case 0:
                return ON;
            case 1:
                return OFF;
        }
        return OFF;
    }

    public ToggleState fromVal(int state) {
        return ToggleState.fromValStatic(state);
    }
}
