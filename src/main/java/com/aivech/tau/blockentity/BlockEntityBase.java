package com.aivech.tau.blockentity;

import com.aivech.tau.Tau;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public abstract class BlockEntityBase extends BlockEntity {

    public BlockEntityBase(BlockEntityType<? extends BlockEntityBase> type) {
        super(type);
    }

    @Override
    public void validate() {
        this.onAdd();
        super.validate();
    }

    protected void onAdd() {
        //Tau.Log.debug("Added BE @ "+this.pos.toString()+this.world.dimension.getType().toString());
    }

    public void onRemove() {
        Tau.Log.debug("onRemove(): " + this.pos.toString() + " in " + this.world.dimension.getType().toString());
    }
}
