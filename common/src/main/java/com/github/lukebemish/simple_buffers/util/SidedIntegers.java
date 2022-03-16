package com.github.lukebemish.simple_buffers.util;

import net.minecraft.nbt.CompoundTag;

public class SidedIntegers extends SidedHolder<Integer>{
    public SidedIntegers() {
        this.left = 0;
        this.right = 0;
        this.front = 0;
        this.back = 0;
        this.up = 0;
        this.down = 0;
    }

    public void fromTag(CompoundTag tag) {
        left = tag.getInt("left");
        right = tag.getInt("right");
        front = tag.getInt("front");
        back = tag.getInt("back");
        up = tag.getInt("up");
        down = tag.getInt("down");
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("left",left);
        tag.putInt("right",right);
        tag.putInt("front",front);
        tag.putInt("back",back);
        tag.putInt("up",up);
        tag.putInt("down",down);

        return tag;
    }
}