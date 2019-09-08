package com.aivech.tau.block;

import com.aivech.tau.blockentity.TauBlockEntities;
import com.aivech.tau.blockentity.BlockEntityBase;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class BEBlockTest extends BlockBase implements BlockEntityProvider {
    public BEBlockTest() {
        super(FabricBlockSettings.of(Material.STONE).build(), "te_test");
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new BlockEntityBase(TauBlockEntities.REGISTRY.get(this.id));
    }
}
