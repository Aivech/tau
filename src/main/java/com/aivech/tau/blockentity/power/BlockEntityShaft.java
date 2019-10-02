package com.aivech.tau.blockentity.power;

import com.aivech.tau.Tau;
import com.aivech.tau.block.power.BlockShaft;
import com.aivech.tau.blockentity.BlockEntityBase;
import com.aivech.tau.blockentity.TauBlockEntities;
import com.aivech.tau.power.GridUpdate;
import com.aivech.tau.power.IRotaryBlock;
import com.google.common.collect.ImmutableSet;
import net.minecraft.util.math.Direction;

import java.util.Set;

public class BlockEntityShaft extends BlockEntityBase implements IRotaryBlock {
    private Direction.Axis axis;
    private final BlockPowerValues values;

    public BlockEntityShaft() {
        super(TauBlockEntities.SHAFT);
        Tau.Log.debug("Shaft constructed");
        values = new BlockPowerValues();
    }

    public void rotateTo(Direction.Axis axis) {
        GridUpdate.removeAll(this.world, this.pos);
        this.axis = axis;
        Direction dir = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        GridUpdate.add(this, this.world, this.pos, dir, new Direction[]{dir, dir.getOpposite()});
    }

    @Override
    public BlockPowerValues getPowerVars() {
        return values;
    }

    @Override
    public Set<Direction> getValidConnections() {
        Tau.Log.debug("Axis: " + this.axis.asString());
        Direction dir = Direction.get(Direction.AxisDirection.POSITIVE, this.axis);
        return ImmutableSet.of(dir, dir.getOpposite());
    }

    @Override
    public void onAdd() {
        super.onAdd();
        this.axis = world.getBlockState(this.pos).get(BlockShaft.AXIS);
        if (this.axis == null) Tau.Log.error("NULL AXIS");
        Direction dir = Direction.get(Direction.AxisDirection.POSITIVE, this.axis);
        GridUpdate.add(this, this.world, this.pos, dir, new Direction[]{dir, dir.getOpposite()});
    }

    @Override
    public void onRemove() {
        super.onRemove();
        GridUpdate.removeAll(this.world, this.pos);
    }
}
