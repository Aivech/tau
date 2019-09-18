package com.aivech.tau.power;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class RotaryNode {

    /**
     * touch this and die in CME hell
     **/
    final ArrayList<RotaryPath> paths = new ArrayList<>();

    protected final BlockPos pos;
    protected final NodeType type;
    protected final Direction dir;
    public RotaryNode(NodeType type, BlockPos pos, Direction dir) {
        this.type = type;
        this.pos = pos;
        this.dir = dir;
    }

    public enum NodeType {
        SOURCE, SINK, PATH
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
        return pos.getY() << 24 + pos.getX() << 14 + pos.getZ() << 4 + this.type.ordinal() << 2 + this.dir.ordinal();
    }
}
