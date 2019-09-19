package com.aivech.tau.power;

import com.aivech.tau.Tau;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RotaryNode {

    /**
     * touch this and die in CME hell
     **/
    final ArrayList<RotaryPath> paths = new ArrayList<>();

    final BlockPos pos;
    final NodeType type;
    final Direction dir;
    final HashSet<Direction> connects;

    RotaryNode(NodeType type, BlockPos pos, Direction dir, Collection<Direction> connectsTo) {
        this.type = type;
        this.pos = pos;
        this.dir = dir;
        this.connects = new HashSet<>(connectsTo);
    }

    boolean canPathTo(RotaryNode neighbor) {
        return true;
    }

    public enum NodeType {
        SOURCE, SINK, PATH, JUNCTION, CLUTCH, MUTATE
    }

    static class Junction extends RotaryNode {
        final boolean merge;
        Junction(NodeType type, BlockPos pos, Direction dir, Collection<Direction> connectsTo, boolean merge) {
            super(type,pos,dir,connectsTo);
            this.merge = merge;
        }

        @Override
        boolean canPathTo(RotaryNode neighbor) {
            Vec3i offset = neighbor.pos.subtract(pos);
            Direction toNeighbor = Direction.fromVector(offset.getX(), offset.getY(),offset.getZ());
            Tau.Log.debug("Neighbor is "+toNeighbor.toString());
            if(neighbor instanceof Junction) {
                Junction junc = (Junction)neighbor;
                if(junc.connects.contains(dir.getOpposite()) && junc.merge == (junc.dir != dir.getOpposite())) {
                    return this.merge == (this.dir == toNeighbor);
                }
            }
            return this.merge == (this.dir == toNeighbor) && neighbor.connects.contains(toNeighbor.getOpposite());
        }


    }


    @Override
    public boolean equals(Object o) {
        if (o == null) { return false; }
        if (o instanceof RotaryNode) {
            RotaryNode node = (RotaryNode)o;
            return (node.pos.equals(this.pos)) && (node.type == this.type) && (node.dir == this.dir);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return pos.getY() << 24 + pos.getX() << 14 + pos.getZ() << 4 + (this.type.ordinal() ^ this.dir.ordinal());
    }
}
