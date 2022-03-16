package com.github.lukebemish.simple_buffers.util;

public interface SidedState {
    public int getVal();
    public SidedState fromVal(int state);
}
