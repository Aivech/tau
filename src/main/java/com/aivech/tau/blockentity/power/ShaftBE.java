package com.aivech.tau.blockentity.power;

import com.aivech.tau.block.power.BlockShaft;
import com.aivech.tau.blockentity.BaseBE;
import com.aivech.tau.blockentity.TauBEs;
import com.aivech.tau.power.GridUpdate;
import com.aivech.tau.power.IRotaryBE;
import com.google.common.collect.ImmutableSet;
import net.minecraft.util.math.Direction;

import java.util.Set;

public class ShaftBE extends BaseBE implements IRotaryBE {
    private final BlockPowerValues values;

    public ShaftBE() {
        super(TauBEs.SHAFT);
        values = new BlockPowerValues();
    }

    public void rotateTo(Direction.Axis axis) {
        GridUpdate.removeAll(this.world, this.pos);
        Direction dir = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        GridUpdate.add(this, this.world, this.pos, dir, new Direction[]{dir, dir.getOpposite()});
    }

    @Override
    public BlockPowerValues getPowerVars() {
        return values;
    }

    @Override
    public Set<Direction> getValidConnections() {
        Direction.Axis axis = world.getBlockState(this.pos).get(BlockShaft.AXIS);
        Direction dir = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        return ImmutableSet.of(dir, dir.getOpposite());
    }

    @Override
    public void onAdd() {
        // super.onAdd();
        Direction.Axis axis = world.getBlockState(this.pos).get(BlockShaft.AXIS);
        Direction dir = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        GridUpdate.add(this, this.world, this.pos, dir, new Direction[]{dir, dir.getOpposite()});
    }

    @Override
    public void onRemove() {
        // super.onRemove();
        GridUpdate.removeAll(this.world, this.pos);
    }
}