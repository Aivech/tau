package com.aivech.tau.power;

import net.minecraft.util.math.Direction;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public interface IRotaryBE {

    BlockPowerValues getPowerVars();

    /**
     * Get all valid connection sides for this block, from all nodes.
     * If the block contains more than one node, this <b>will not</b> be the same as the values passed to GridUpdate.add().
     *
     * @return The directions of all valid connection sides.
     */
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
