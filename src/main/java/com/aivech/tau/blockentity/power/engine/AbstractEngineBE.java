package com.aivech.tau.blockentity.power.engine;

import com.aivech.tau.blockentity.BaseBE;
import com.aivech.tau.power.IRotarySource;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Direction;

public abstract class AbstractEngineBE extends BaseBE implements IRotarySource {
    public AbstractEngineBE(BlockEntityType<? extends BaseBE> type) {
        super(type);
    }

    public abstract void rotateTo(Direction dir);
}
