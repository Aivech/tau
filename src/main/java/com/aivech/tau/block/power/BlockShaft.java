package com.aivech.tau.block.power;

import com.aivech.tau.Tau;
import com.aivech.tau.block.BlockBase;
import com.aivech.tau.block.IRotatable;
import com.aivech.tau.blockentity.power.ShaftBE;
import com.aivech.tau.power.IRotaryBE;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class BlockShaft extends BlockBase implements BlockEntityProvider, IRotatable {
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;

    public BlockShaft() {
        super(FabricBlockSettings.of(Material.METAL).build(), "shaft");
        this.setDefaultState(this.getDefaultState().with(AXIS, Direction.Axis.Z));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new ShaftBE();
    }

    @Override
    public boolean rotate(BlockState blockState, World world, BlockPos pos, PlayerEntity player) {
        Direction.Axis axis;
        switch (blockState.get(AXIS)) {
            case Z:
                axis = Direction.Axis.X;
                break;
            case X:
                axis = Direction.Axis.Y;
                break;
            case Y:
                axis = Direction.Axis.Z;
                break;
            default:
                throw new UnsupportedOperationException("Attempted to rotate a BlockShaft with invalid blockstate. This should never happen.");
        }
        world.setBlockState(pos, blockState.with(AXIS, axis));
        ShaftBE be = (ShaftBE)world.getBlockEntity(pos);
        be.rotateTo(axis);
        return true;
    }

    @Override
    public boolean invRotate(BlockState blockState, World world, BlockPos pos, PlayerEntity player) {
        Direction.Axis axis;
        switch (blockState.get(AXIS)) {
            case Z:
                axis = Direction.Axis.Y;
                break;
            case X:
                axis = Direction.Axis.Z;
                break;
            case Y:
                axis = Direction.Axis.X;
                break;
            default:
                throw new UnsupportedOperationException("Attempted to rotate a BlockShaft with invalid blockstate. This should never happen.");
        }
        world.setBlockState(pos, blockState.with(AXIS, axis));
        ShaftBE be = (ShaftBE)world.getBlockEntity(pos);
        be.rotateTo(axis);
        return true;

        // BlockState$cycleProperty
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> stateFactory$Builder_1) {
        stateFactory$Builder_1.add(AXIS);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        World w = context.getWorld();
        BlockPos pos = context.getBlockPos();
        Direction dir = context.getSide().getOpposite();

        ArrayList<Direction> found = new ArrayList<>();
        for (Direction d : Direction.values()) {
            BlockEntity be = w.getBlockEntity(pos.offset(d));
            if (be instanceof IRotaryBE) {
                Tau.Log.debug(be.getClass().toString());
                if (((IRotaryBE)be).getValidConnections().contains(d.getOpposite())) {
                    if (d == dir)
                        return this.getDefaultState().with(AXIS, dir.getAxis());
                    found.add(d);
                }
            }
        }
        switch (found.size()) {
            case 1:
                return this.getDefaultState().with(AXIS, found.get(0).getAxis());
            case 2:
                if (found.get(0) == found.get(1).getOpposite())
                    return this.getDefaultState().with(AXIS, found.get(0).getAxis());
        }
        return this.getDefaultState().with(AXIS, context.getPlayerFacing().getAxis());
    }
}
