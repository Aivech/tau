package com.aivech.tau.block.power;

import com.aivech.tau.blockentity.power.engine.DebugEngineBE;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

import javax.annotation.Nullable;

public class DebugEngineBlock extends EngineBlock {
    public DebugEngineBlock() {
        super("debug_engine");
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new DebugEngineBE();
    }
}
