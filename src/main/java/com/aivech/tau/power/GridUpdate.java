package com.aivech.tau.power;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.Arrays;
import java.util.Collection;

public class GridUpdate {
    final GridAction action;
    final RotaryNode node;

    private GridUpdate(GridAction action, RotaryNode node) {
        this.node = node;
        this.action = action;
    }

    public static void add(IRotaryBlock block, World world, BlockPos blockPos, Direction orient, Collection<Direction> connectsTo) {
        RotaryNode.NodeType type;
        if (block instanceof IRotaryUser) {
            type = RotaryNode.NodeType.SINK;
        } else if (block instanceof IRotaryProvider) {
            type = RotaryNode.NodeType.SOURCE;
        } else {
            type = RotaryNode.NodeType.PATH;
        }
        RotaryNode node = new RotaryNode(RotaryNode.NodeType.SINK, blockPos, orient, connectsTo);

        RotaryGrid.UPDATE_QUEUES.get(DimensionType.getId(world.getDimension().getType())).add(new GridUpdate(GridAction.ADD,node));
    }

    // convenience method because Direction.ALL is an array
    public static void add(IRotaryBlock block, World world, BlockPos blockPos, Direction orient, Direction[] connectsTo) {
        add(block, world, blockPos, orient, Arrays.asList(connectsTo));
    }

    enum GridAction {
        ADD, DEL, UPDATE
    }
}
