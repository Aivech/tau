package com.aivech.tau.power;

import net.minecraft.util.math.Direction;

public interface IRotaryBlock {
    boolean canConnectToSide(Direction dir);
}
