package com.aivech.tau.power;

import net.minecraft.util.math.Direction;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public interface IRotaryBlock {

    BlockPowerValues getPowerVars();

    Set<Direction> getValidConnections();

    class BlockPowerValues {
        public final AtomicInteger torqueIn = new AtomicInteger();
        public final AtomicInteger speedIn = new AtomicInteger();
        public final AtomicInteger torqueOut = new AtomicInteger();
        public final AtomicInteger speedOut = new AtomicInteger();

        void update(int torqueIn, int speedIn, int torqueOut, int speedOut) {
            this.torqueIn.set(torqueIn);
            this.speedIn.set(speedIn);
            this.torqueOut.set(torqueOut);
            this.speedOut.set(speedOut);
        }
    }
}
