package com.aivech.tau.block.power;

import com.aivech.tau.Tau;
import com.aivech.tau.block.BlockBase;
import com.aivech.tau.block.IRotatable;
import com.aivech.tau.blockentity.power.engine.AbstractEngineBE;
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
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public abstract class EngineBlock extends BlockBase implements BlockEntityProvider, IRotatable {

    public EngineBlock(String id) {
        super(FabricBlockSettings.of(Material.METAL).build(), id);
        this.setDefaultState(this.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> stateFactory$Builder_1) {
        stateFactory$Builder_1.add(Properties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        World w = context.getWorld();
        BlockPos pos = context.getBlockPos();
        Direction dir = context.getSide().getOpposite();

        ArrayList<Direction> found = new ArrayList<>();
        for (int i = 2; i < 6; i++) {
            Direction d = Direction.byId(i);
            BlockEntity be = w.getBlockEntity(pos.offset(d));
            if (be instanceof IRotaryBE) {
                Tau.Log.debug(be.getClass().toString());
                if (((IRotaryBE)be).getValidConnections().contains(d.getOpposite())) {
                    if (d == dir)
                        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, d);
                    found.add(d);
                }
            }
        }
        if (found.size() == 1) {
            return this.getDefaultState().with(Properties.HORIZONTAL_FACING, found.get(0));
        }
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, context.getPlayerFacing());
    }

    @Override
    public boolean rotate(BlockState blockState, World world, BlockPos pos, PlayerEntity player) {
        Direction dir = blockState.get(Properties.HORIZONTAL_FACING).rotateYClockwise();
        world.setBlockState(pos, blockState.with(Properties.HORIZONTAL_FACING, dir));
        AbstractEngineBE be = (AbstractEngineBE)world.getBlockEntity(pos);
        be.rotateTo(dir);
        return true;
    }

    @Override
    public boolean invRotate(BlockState blockState, World world, BlockPos pos, PlayerEntity player) {
        Direction dir = blockState.get(Properties.HORIZONTAL_FACING).rotateYCounterclockwise();
        world.setBlockState(pos, blockState.with(Properties.HORIZONTAL_FACING, dir));
        AbstractEngineBE be = (AbstractEngineBE)world.getBlockEntity(pos);
        be.rotateTo(dir);
        return true;
    }
}
