package com.aivech.tau.blockentity;

import com.aivech.tau.block.BlockBase;
import com.aivech.tau.block.TauBlocks;
import com.aivech.tau.blockentity.power.BlockEntityShaft;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class TauBlockEntities {

    public static BlockEntityType<BlockEntityShaft> SHAFT;

    public static void init() {
        SHAFT = register(BlockEntityShaft::new, TauBlocks.SHAFT);
    }

    private static BlockEntityType register(Supplier<BlockEntityShaft> c, BlockBase b) {
        return Registry.register(
                Registry.BLOCK_ENTITY,
                b.id,
                new BlockEntityType<>(c, ImmutableSet.of(b), null));
    }
}