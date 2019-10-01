package com.aivech.tau.block.power;

import com.aivech.tau.block.BlockBase;
import com.aivech.tau.blockentity.power.BlockEntityShaft;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

import javax.annotation.Nullable;

public class BlockShaft extends BlockBase implements BlockEntityProvider {

    public BlockShaft() {
        super(FabricBlockSettings.of(Material.METAL).build(), "shaft");
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new BlockEntityShaft();
    }
}
