package com.aivech.tau.power;

import com.aivech.tau.Tau;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class RotaryNode {

    /**
     * touch these and die in CME hell
     **/
    final ArrayList<RotaryPath> paths = new ArrayList<>();
    final HashMap<RotaryPath, NodePowerValues> pathPowerMap = new HashMap<>();

    final BlockPos pos;
    final NodeType type;
    final Direction orient;
    final HashSet<Direction> connects;

    RotaryNode(NodeType type, BlockPos pos, Direction orient, Collection<Direction> connectsTo) {
        this.type = type;
        this.pos = pos;
        this.orient = orient;
        this.connects = new HashSet<>(connectsTo);
    }

    abstract boolean canReceivePowerFrom(RotaryNode neighbor);

    // add fractional path power and update transformation
    abstract void handleTransaction(GridTransaction trans, RotaryPath path);

    // pull new values from world
    abstract void updateNode();

    // push new updates to world
    abstract void updateBlock();

    static class GridTransaction {
        int torqueFactor = 1;
        int speedFactor = 1;
    }

    enum NodeType {
        SOURCE, SINK, PATH, JUNCTION, CLUTCH, TRANSFORM
    }

    static class Source extends RotaryNode {
        private final AtomicInteger blockSpeed;
        private final AtomicInteger blockTorque;

        private int sourceTorque;
        private int sourceSpeed;

        Source(BlockPos pos, Direction dir, Collection<Direction> connectsTo, AtomicInteger torque, AtomicInteger speed) {
            super(NodeType.SOURCE, pos, dir, connectsTo);
            this.blockTorque = torque;
            this.blockSpeed = speed;
        }

        @Override
        void handleTransaction(GridTransaction trans, RotaryPath path) {
        }

        @Override
        boolean canReceivePowerFrom(RotaryNode neighbor) {
            return false;
        }

        @Override
        void updateNode() {
            this.sourceSpeed = blockSpeed.get();
            this.sourceTorque = blockTorque.get();
        }

        @Override
        void updateBlock() {
        }

        int getFractionalTorque() {
            return sourceTorque / paths.size();
        }
    }

    static class Clutch extends RotaryNode {
        private final AtomicBoolean blockEngaged;
        private boolean engaged = false;


        Clutch(BlockPos pos, Direction dir, Collection<Direction> connectsTo, AtomicBoolean blockEngaged) {
            super(NodeType.CLUTCH, pos, dir, connectsTo);
            this.blockEngaged = blockEngaged;
        }

        @Override
        void handleTransaction(GridTransaction t, RotaryPath path) {
            if (t.torqueFactor * t.speedFactor == 0) {
                pathPowerMap.put(path, NodePowerValues.ZERO);
                return;
            }
            int torqueIn = path.source.getFractionalTorque() * t.torqueFactor / t.speedFactor;
            int speedIn = path.source.sourceSpeed * t.speedFactor / t.torqueFactor;
            if (! engaged) {
                t.torqueFactor = 0;
                t.speedFactor = 0;
                pathPowerMap.put(path, new NodePowerValues(torqueIn, speedIn, 0, 0));
                return;
            }
            pathPowerMap.put(path, new NodePowerValues(torqueIn, speedIn, torqueIn, speedIn));
        }

        @Override
        boolean canReceivePowerFrom(RotaryNode neighbor) {
            return true;
        }

        @Override
        void updateNode() {
            engaged = blockEngaged.get();
        }

        @Override
        void updateBlock() {

        }
    }

    static class Transform extends RotaryNode {
        private int torqueFactor;
        private int speedFactor;

        Transform(IRotaryBlock block, BlockPos pos, Direction dir, Collection<Direction> connectsTo, int torqueFactor, int speedFactor) {
            super(NodeType.TRANSFORM, pos, dir, connectsTo);
            this.torqueFactor = torqueFactor;
            this.speedFactor = speedFactor;
        }

        @Override
        void handleTransaction(GridTransaction t, RotaryPath path) {
            if (t.torqueFactor * t.speedFactor == 0) {
                pathPowerMap.put(path, NodePowerValues.ZERO);
                return;
            }
            int inputTorque = path.source.getFractionalTorque() * t.torqueFactor / t.speedFactor;
            int inputSpeed = path.source.sourceSpeed * t.speedFactor / t.speedFactor;
            t.torqueFactor *= torqueFactor;
            t.speedFactor *= speedFactor;
            t.torqueFactor /= speedFactor;
            t.speedFactor /= torqueFactor;
            pathPowerMap.put(path, new GridTransaction(path.source.getFractionalTorque()))
        }

        @Override
        boolean canReceivePowerFrom(RotaryNode neighbor) {
            return true;
        }
    }


    static class Junction extends RotaryNode {
        final boolean merge;

        Junction(IRotaryBlock block, BlockPos pos, Direction dir, Collection<Direction> connectsTo, boolean merge) {
            super(NodeType.JUNCTION, pos, dir, connectsTo);
            this.merge = merge;
        }

        @Override
        boolean canPathTo(RotaryNode neighbor) {
            Vec3i offset = neighbor.pos.subtract(pos);
            Direction toNeighbor = Direction.fromVector(offset.getX(), offset.getY(), offset.getZ());
            Tau.Log.debug("Neighbor is " + toNeighbor.toString());
            if (neighbor instanceof Junction) {
                Junction junc = (Junction)neighbor;
                if (junc.connects.contains(orient.getOpposite()) && junc.merge == (junc.orient != orient.getOpposite())) {
                    return this.merge == (this.orient == toNeighbor);
                }
            }
            return this.merge == (this.orient == toNeighbor) && neighbor.connects.contains(toNeighbor.getOpposite());
        }


    }

    private static class NodePowerValues {
        static final NodePowerValues ZERO = new NodePowerValues(0, 0, 0, 0);

        private final int torqueIn;
        private final int speedIn;
        private final int torqueOut;
        private final int speedOut;

        private NodePowerValues(int torqueIn, int speedIn, int torqueOut, int speedOut) {
            this.torqueIn = torqueIn;
            this.speedIn = speedIn;
            this.torqueOut = torqueOut;
            this.speedOut = speedOut;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof RotaryNode) {
            RotaryNode node = (RotaryNode)o;
            return (node.pos.equals(this.pos)) && (node.type == this.type) && (node.orient == this.orient);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return pos.getY() << 24 + pos.getX() << 14 + pos.getZ() << 4 + (this.type.ordinal() ^ this.orient.ordinal());
    }
}
