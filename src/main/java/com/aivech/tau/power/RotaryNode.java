package com.aivech.tau.power;

import com.aivech.tau.Tau;
import com.aivech.tau.power.IRotaryBE.BlockPowerValues;
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
    final BlockPowerValues blockValues;

    RotaryNode(NodeType type, BlockPos pos, Direction orient, Collection<Direction> connectsTo, BlockPowerValues blockValues) {
        this.blockValues = blockValues;
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
    void updateBlock() {
        int torqueIn = 0;
        int speedIn = 0;
        int torqueOut = 0;
        int speedOut = 0;
        boolean initial = true;
        for (NodePowerValues nodeValues : pathPowerMap.values()) {
            if (! initial) {
                if (speedIn != nodeValues.speedIn || speedOut != nodeValues.speedOut) {
                    // todo: blow stuff up / other consequences?
                    break;
                }
            } else {
                speedIn = nodeValues.speedIn;
                speedOut = nodeValues.speedOut;
                initial = false;
            }
            torqueIn += nodeValues.torqueIn;
            torqueOut += nodeValues.torqueOut;
        }
        this.blockValues.update(torqueIn, speedIn, torqueOut, speedOut);
    }

    static class GridTransaction {
        int torqueFactor = 1;
        int speedFactor = 1;
    }

    enum NodeType {
        SOURCE, SINK, PATH, JUNCTION, TRANSFORM
    }

    static class Source extends RotaryNode {
        private int sourceTorque;
        private int sourceSpeed;

        Source(BlockPos pos, Direction dir, Collection<Direction> connectsTo, BlockPowerValues blockPower) {
            super(NodeType.SOURCE, pos, dir, connectsTo, blockPower);
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
            this.sourceSpeed = blockValues.speedOut.get();
            this.sourceTorque = blockValues.torqueOut.get();
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


        Clutch(BlockPos pos, Direction dir, Collection<Direction> connectsTo, AtomicBoolean blockEngaged, BlockPowerValues blockPower) {
            super(NodeType.TRANSFORM, pos, dir, connectsTo, blockPower);
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

    }

    static class Transform extends RotaryNode {
        private AtomicInteger blockTorqueFactor;
        private AtomicInteger blockSpeedFactor;
        private int torqueFactor;
        private int speedFactor;

        Transform(BlockPos pos, Direction dir, Collection<Direction> connectsTo, AtomicInteger torqueFactor, AtomicInteger speedFactor, BlockPowerValues values) {
            super(NodeType.TRANSFORM, pos, dir, connectsTo, values);
            this.blockTorqueFactor = torqueFactor;
            this.blockSpeedFactor = speedFactor;
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
            pathPowerMap.put(path, new NodePowerValues(inputTorque, inputSpeed,
                    path.source.getFractionalTorque() * t.torqueFactor, path.source.sourceSpeed * t.speedFactor));
        }

        @Override
        void updateNode() {
            this.torqueFactor = blockTorqueFactor.get();
            this.speedFactor = blockSpeedFactor.get();
        }

        @Override
        boolean canReceivePowerFrom(RotaryNode neighbor) {
            return true;
        }
    }


    static class Junction extends RotaryNode {
        final boolean merge;

        Junction(BlockPos pos, Direction dir, Collection<Direction> connectsTo, boolean merge, BlockPowerValues values) {
            super(NodeType.JUNCTION, pos, dir, connectsTo, values);
            this.merge = merge;
        }

        @Override
        boolean canReceivePowerFrom(RotaryNode neighbor) {
            Vec3i offset = neighbor.pos.subtract(pos);
            Direction toNeighbor = Direction.fromVector(offset.getX(), offset.getY(), offset.getZ());
            Tau.Log.debug("Neighbor is " + toNeighbor.toString());
            if (neighbor instanceof Junction) {
                Junction neighborJunc = (Junction)neighbor;
                if (this.merge) {
                    if (toNeighbor == this.orient) return false;
                    if (neighborJunc.merge) {
                        return neighborJunc.orient == toNeighbor.getOpposite();
                    } else {
                        return neighborJunc.orient != toNeighbor.getOpposite();
                    }
                } else {
                    if (toNeighbor != this.orient) return false;
                    if (neighborJunc.merge) {
                        return neighborJunc.orient == toNeighbor.getOpposite();
                    } else {
                        return neighborJunc.orient != toNeighbor.getOpposite();
                    }
                }
            }
            return this.merge ? this.orient != toNeighbor : this.orient == toNeighbor;
        }

        @Override
        void updateNode() {
        }

        @Override
        void handleTransaction(GridTransaction t, RotaryPath path) {
            if (t.torqueFactor * t.speedFactor == 0) {
                pathPowerMap.put(path, NodePowerValues.ZERO);
                return;
            }
            int torqueIn = path.source.getFractionalTorque() * t.torqueFactor / t.speedFactor;
            int speedIn = path.source.sourceSpeed * t.speedFactor / t.torqueFactor;
            pathPowerMap.put(path, new NodePowerValues(torqueIn, speedIn, torqueIn, speedIn));
        }
    }

    static class Path extends RotaryNode {
        Path(BlockPos pos, Direction orient, Collection<Direction> connectsTo, BlockPowerValues blockValues) {
            super(NodeType.PATH, pos, orient, connectsTo, blockValues);
        }

        @Override
        boolean canReceivePowerFrom(RotaryNode neighbor) {
            return true;
        }

        @Override
        void handleTransaction(GridTransaction t, RotaryPath path) {
            if (t.torqueFactor * t.speedFactor == 0) {
                pathPowerMap.put(path, NodePowerValues.ZERO);
                return;
            }
            int torqueIn = path.source.getFractionalTorque() * t.torqueFactor / t.speedFactor;
            int speedIn = path.source.sourceSpeed * t.speedFactor / t.torqueFactor;
            pathPowerMap.put(path, new NodePowerValues(torqueIn, speedIn, torqueIn, speedIn));
        }

        @Override
        void updateNode() {
        }
    }

    static class Sink extends RotaryNode {
        Sink(BlockPos pos, Direction orient, Collection<Direction> connectsTo, BlockPowerValues blockValues) {
            super(NodeType.SINK, pos, orient, connectsTo, blockValues);
        }

        @Override
        boolean canReceivePowerFrom(RotaryNode neighbor) {
            return true;
        }

        @Override
        void handleTransaction(GridTransaction t, RotaryPath path) {
            if (t.torqueFactor * t.speedFactor == 0) {
                pathPowerMap.put(path, NodePowerValues.ZERO);
                return;
            }
            int torqueIn = path.source.getFractionalTorque() * t.torqueFactor / t.speedFactor;
            int speedIn = path.source.sourceSpeed * t.speedFactor / t.torqueFactor;
            pathPowerMap.put(path, new NodePowerValues(torqueIn, speedIn, 0, 0));
        }

        @Override
        void updateNode() {
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
