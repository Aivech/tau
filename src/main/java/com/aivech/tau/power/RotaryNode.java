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

    int torqueIn;
    int speedIn;
    int torqueOut;
    int speedOut;

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

    boolean canPathTo(RotaryNode neighbor) {
        return true;
    }

    boolean handleTransaction(GridTransaction t) {
        return false;
    }

    void addPathInputPower(GridTransaction trans, Source source) {

    }

    abstract void addPathOutputPower(GridTransaction trans, Source source);

    abstract void updateNode();

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


        Source(BlockPos pos, Direction dir, Collection<Direction> connectsTo, AtomicInteger torque, AtomicInteger speed) {
            super(NodeType.SOURCE, pos, dir, connectsTo);
            this.blockTorque = torque;
            this.blockSpeed = speed;
        }

        @Override
        void addPathInputPower(GridTransaction trans, Source source) {
        }

        @Override
        void addPathOutputPower(GridTransaction trans, Source source) {
        }

        @Override
        void updateNode() {
            this.speedOut = blockSpeed.get();
            this.torqueOut = blockTorque.get();
        }

        @Override
        void updateBlock() {
        }

        int getFractionalTorque() {
            return torqueOut / paths.size();
        }
    }

    static class Clutch extends RotaryNode {
        private AtomicBoolean blockEngaged;
        private boolean engaged = false;

        Clutch(IRotaryBlock block, BlockPos pos, Direction dir, Collection<Direction> connectsTo, AtomicBoolean blockEngaged) {
            super(NodeType.CLUTCH, pos, dir, connectsTo);
            this.blockEngaged = blockEngaged;
        }

        @Override
        boolean handleTransaction(GridTransaction t) {
            if (! engaged) {
                t.torqueFactor *= 0;
                t.speedFactor *= 0;
            }
            return true;
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
        void handleTransaction(GridTransaction t) {
            if (torqueFactor * speedFactor == 0) return;
            t.torqueFactor *= torqueFactor;
            t.speedFactor *= speedFactor;
            t.torqueFactor /= speedFactor;
            t.speedFactor /= torqueFactor;
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
        private int torqueIn = 0;
        private int speedIn = 0;
        private int torqueOut = 0;
        private int speedOut = 0;

        void setInputs(int torqueIn, int speedIn) {
            this.torqueIn = torqueIn;
            this.speedIn = speedIn;
        }

        void setOutputs(int torqueOut, int speedOut) {
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
