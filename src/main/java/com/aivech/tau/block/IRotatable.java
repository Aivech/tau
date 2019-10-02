package com.aivech.tau.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRotatable {

    /**
     * Called when the block is right-clicked with a rotation tool (screwdriver, wrench, whatever).
     *
     * @return true if the block's rotation changed.
     */
    boolean rotate(BlockState blockState, World world, BlockPos pos, PlayerEntity player);

    /**
     * Called when the block is shift-right-clicked with a rotation tool.
     *
     * @return true if the block's rotation changed.
     */
    boolean invRotate(BlockState blockState, World world, BlockPos pos, PlayerEntity player);
}
