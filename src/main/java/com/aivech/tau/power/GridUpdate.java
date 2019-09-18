package com.aivech.tau.power;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.Arrays;
import java.util.Collection;

public abstract class GridUpdate {
    final GridAction action;

    private GridUpdate(GridAction action) {
        this.action = action;
    }

    static class Add extends GridUpdate {
        final RotaryNode node;

        private Add(RotaryNode node) {
            super(GridAction.ADD);
            this.node = node;
        }
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

        RotaryGrid.UPDATE_QUEUES.get(DimensionType.getId(world.getDimension().getType())).add(
                new GridUpdate.Add(node)
        );
    }

    // convenience method because Direction.ALL is an array
    public static void add(IRotaryBlock block, World world, BlockPos blockPos, Direction orient, Direction[] connectsTo) {
        add(block, world, blockPos, orient, Arrays.asList(connectsTo));
    }

    public enum GridAction {
        ADD, DEL, UPDATE
    }
}
