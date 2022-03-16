package com.github.lukebemish.simple_buffers.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class ToggleableSlot extends Slot {
    public boolean toggleState = false;

    public ToggleableSlot(Container container, int i, int j, int k) {
        super(container, i, j, k);
    }

    public void turnOn() {
        toggleState = true;
    }

    public void turnOff() {
        toggleState = false;
    }

    public boolean isActive() {
        return this.toggleState;
    }
}
