package com.aivech.tau.power;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class WorldPowerUpdate {
    final BlockPos pos;
    final Direction orient;

    private int torqueIn;
    private int speedIn;
    private int torqueOut;
    private int speedOut;

    private boolean built;

    WorldPowerUpdate(RotaryNode n) {
        pos = n.pos;
        orient = n.orient;
    }

    void setInputValues(RotaryNode.GridTransaction transact, RotaryNode.Source source) {
        if (transact.speedFactor * transact.torqueFactor == 0) {
            this.speedIn = this.torqueIn = 0;
        }
        this.torqueIn = source.getFractionalTorque() * transact.torqueFactor / transact.speedFactor;
        this.speedIn = source.speed * transact.speedFactor / transact.torqueFactor;
    }

    void setOutputValues(RotaryNode.GridTransaction transact, RotaryNode.Source source) {
        if (transact.speedFactor * transact.torqueFactor == 0) {
            this.speedIn = this.torqueIn = 0;
        }
        this.torqueOut = source.getFractionalTorque() * transact.torqueFactor / transact.speedFactor;
        this.speedOut = source.speed * transact.speedFactor / transact.torqueFactor;
    }
}
