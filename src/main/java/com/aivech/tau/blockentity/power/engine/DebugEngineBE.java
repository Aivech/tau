package com.aivech.tau.blockentity.power.engine;

import com.aivech.tau.blockentity.TauBEs;
import com.aivech.tau.power.GridUpdate;
import com.google.common.collect.ImmutableSet;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

import java.util.Set;

public class DebugEngineBE extends AbstractEngineBE {
    private static final BlockPowerValues values = new BlockPowerValues();

    public DebugEngineBE() {
        super(TauBEs.DEBUG_ENGINE);
    }

    @Override
    public void rotateTo(Direction dir) {
        GridUpdate.removeAll(this.world, this.pos);
        GridUpdate.add(this, this.world, this.pos, dir, new Direction[]{dir});
    }

    @Override
    public BlockPowerValues getPowerVars() {
        return values;
    }

    @Override
    public Set<Direction> getValidConnections() {
        return ImmutableSet.of(this.world.getBlockState(this.pos).get(Properties.HORIZONTAL_FACING));
    }

    @Override
    public void onAdd() {
        Direction dir = world.getBlockState(this.pos).get(Properties.HORIZONTAL_FACING);
        GridUpdate.add(this, this.world, this.pos, dir, new Direction[]{dir});
    }

    @Override
    public void onRemove() {
        GridUpdate.removeAll(this.world, this.pos);
    }
}
