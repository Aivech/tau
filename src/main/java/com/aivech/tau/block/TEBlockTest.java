package com.aivech.tau.block;

import com.aivech.tau.tileentity.TauTileEntities;
import com.aivech.tau.tileentity.TileEntityBase;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class TEBlockTest extends BlockBase implements BlockEntityProvider {
    public TEBlockTest() {
        super(FabricBlockSettings.of(Material.STONE).build(), "te_test");
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new TileEntityBase(TauTileEntities.REGISTRY.get(this.id));
    }
}
