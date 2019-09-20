package com.aivech.tau.power;

import com.aivech.tau.power.RotaryNode.NodeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.Arrays;
import java.util.Collection;

public class GridUpdate {
    final UpdateAction action;
    RotaryNode node;
    BlockPos pos;
    Direction dir;

    private GridUpdate(UpdateAction action, RotaryNode node, BlockPos pos, Direction dir) {
        this.node = node;
        this.action = action;
        this.pos = pos;
        this.dir = dir;
    }

    public static void add(IRotaryBlock block, World world, BlockPos blockPos, Direction orient, Collection<Direction> connectsTo) {
        RotaryNode node;
        if (block instanceof IRotaryUser) {
            node = new RotaryNode(NodeType.SINK, blockPos, orient, connectsTo);
        } else if (block instanceof IRotaryProvider) {
            node = new RotaryNode.Source(blockPos, orient, connectsTo);
        } else if (block instanceof IRotaryTransform) {
            IRotaryTransform xform = (IRotaryTransform)block;
            node = new RotaryNode.Transform(blockPos, orient, connectsTo, xform.getTorqueFactor().get(), xform.getSpeedFactor().get());
        } else if (block instanceof IRotaryClutch) {
            IRotaryClutch clutch = (IRotaryClutch)block;
            node = new RotaryNode.Clutch(blockPos, orient, connectsTo, clutch.isEngaged().get());
        } else {
            node = new RotaryNode(NodeType.PATH, blockPos, orient, connectsTo);
        }

        Identifier id = DimensionType.getId(world.getDimension().getType());

        RotaryGrid.UPDATE_QUEUES.get(id).add(new GridUpdate(UpdateAction.ADD, node, null, null));
        Object lock = RotaryGrid.LOCK_OBJECTS.get(id);
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    // convenience method because Direction.ALL is an array
    public static void add(IRotaryBlock block, World world, BlockPos blockPos, Direction orient, Direction[] connectsTo) {
        add(block, world, blockPos, orient, Arrays.asList(connectsTo));
    }

    public static void remove(World world, BlockPos pos, Direction orient) {
        Identifier id = DimensionType.getId(world.getDimension().getType());
        RotaryGrid.UPDATE_QUEUES.get(id).add(new GridUpdate(UpdateAction.ADD, null, pos, orient));
        Object lock = RotaryGrid.LOCK_OBJECTS.get(id);
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public static void removeAll(World world, BlockPos pos) {
        remove(world,pos,null);
    }

    public static void update(World world, BlockPos pos, Direction orient) {
        Identifier id = DimensionType.getId(world.getDimension().getType());
        RotaryGrid.UPDATE_QUEUES.get(id).add(new GridUpdate(UpdateAction.UPDATE, null, pos, orient));
        Object lock = RotaryGrid.LOCK_OBJECTS.get(id);
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    enum UpdateAction {
        ADD, DEL, UPDATE
    }
}
