package com.aivech.tau.blockentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class BlockEntityBase extends BlockEntity {

    public BlockEntityBase(BlockEntityType<BlockEntityBase> type) {
        super(type);
    }


}