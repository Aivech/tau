package com.aivech.tau.power;

import java.util.concurrent.atomic.AtomicInteger;

public interface IRotaryBlock {

    BlockPowerValues getPowerVars();

    class BlockPowerValues {
        private final AtomicInteger torqueIn;
        private final AtomicInteger speedIn;
        final AtomicInteger torqueOut;
        final AtomicInteger speedOut;

        public BlockPowerValues(AtomicInteger torqueIn, AtomicInteger speedIn, AtomicInteger torqueOut, AtomicInteger speedOut) {
            this.torqueIn = torqueIn;
            this.speedIn = speedIn;
            this.torqueOut = torqueOut;
            this.speedOut = speedOut;
        }

        void update(int torqueIn, int speedIn, int torqueOut, int speedOut) {
            this.torqueIn.set(torqueIn);
            this.speedIn.set(speedIn);
            this.torqueOut.set(torqueOut);
            this.speedOut.set(speedOut);
        }
    }
}
