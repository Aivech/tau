package com.aivech.tau.blockentity.power;

import com.aivech.tau.blockentity.BlockEntityBase;
import com.aivech.tau.blockentity.TauBlockEntities;
import com.aivech.tau.power.IRotaryBlock;

public class BlockEntityShaft extends BlockEntityBase implements IRotaryBlock {

    public BlockEntityShaft() {
        super(TauBlockEntities.SHAFT);
    }

    @Override
    public BlockPowerValues getPowerVars() {
        return null;
    }
}
