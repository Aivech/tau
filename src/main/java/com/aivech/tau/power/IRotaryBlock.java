package com.aivech.tau.power;

import net.minecraft.util.math.Direction;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public interface IRotaryBlock {
    boolean canConnectToSide(Direction dir);

    void setPowerValues(AtomicInteger torqueIn, AtomicBoolean speedIn, AtomicInteger torqueOut, AtomicInteger speedOut);
}
