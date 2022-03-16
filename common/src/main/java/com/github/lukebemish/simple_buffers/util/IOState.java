package com.github.lukebemish.simple_buffers.util;

public enum IOState implements SidedState{
    NONE(0),
    IN(1),
    OUT(2),
    INOUT(3);

    private final int state;

    private IOState(int state) {
        this.state = state;
    }

    public int getVal() {
        return this.state;
    }

    public static IOState fromValStatic(int state) {
        switch(state) {
            case 0:
                return NONE;
            case 1:
                return IN;
            case 2:
                return OUT;
            case 3:
                return INOUT;
        }
        return NONE;
    }

    public IOState fromVal(int state) {
        return IOState.fromValStatic(state);
    }

    public boolean isIn() {
        if (this == IN || this == INOUT) {
            return true;
        }
        return false;
    }

    public boolean isOut() {
        if (this == OUT || this == INOUT) {
            return true;
        }
        return false;
    }
}
