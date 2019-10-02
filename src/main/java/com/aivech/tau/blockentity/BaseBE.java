package com.aivech.tau.blockentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public abstract class BaseBE extends BlockEntity implements IAddRemoveNotifiable {

    public BaseBE(BlockEntityType<? extends BaseBE> type) {
        super(type);
    }

    @Override
    public void validate() {
        super.validate();
    }

    /*
    @Override
    public void onAdd() {
        Tau.Log.debug("onAdd(): " + this.pos.toString() + " in " + this.world.dimension.getType().toString());
    }

    @Override
    public void onRemove() {
        Tau.Log.debug("onRemove(): " + this.pos.toString() + " in " + this.world.dimension.getType().toString());
    }*/
}
