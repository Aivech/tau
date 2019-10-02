package com.aivech.tau.blockentity;

import com.aivech.tau.block.BlockBase;
import com.aivech.tau.block.TauBlocks;
import com.aivech.tau.blockentity.power.ShaftBE;
import com.aivech.tau.blockentity.power.engine.DebugEngineBE;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class TauBEs {

    public static BlockEntityType<ShaftBE> SHAFT;
    public static BlockEntityType<DebugEngineBE> DEBUG_ENGINE;

    public static void init() {
        SHAFT = register(ShaftBE::new, TauBlocks.SHAFT);
        DEBUG_ENGINE = register(DebugEngineBE::new, TauBlocks.DEBUG_ENGINE);
    }

    private static BlockEntityType register(Supplier<BaseBE> c, BlockBase b) {
        return Registry.register(
                Registry.BLOCK_ENTITY,
                b.id,
                new BlockEntityType<>(c, ImmutableSet.of(b), null));
    }
}