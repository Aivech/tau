package com.aivech.tau.power;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class RotaryPath {
    final RotaryNode firstNode;
    final LinkedHashSet<RotaryNode> nodeSet = new LinkedHashSet<>();
    private RotaryNode lastNode;

    public RotaryPath(RotaryNode n) {
        firstNode = n;
        lastNode = n;
    }

    public RotaryPath copy() {
        RotaryPath copy = new RotaryPath(this.firstNode);
        for(RotaryNode node : nodeSet) {
            copy.append(node);
        }
        return copy;
    }

    public void append(RotaryNode n) {
        lastNode = n;
        nodeSet.add(n);
    }

    public boolean contains(RotaryNode n) {
        return nodeSet.contains(n);
    }

    public RotaryNode getLast() {
        return lastNode;
    }

    @Override
    public int hashCode() {
        return firstNode.hashCode() ^ lastNode.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof RotaryPath && ((RotaryPath)o).nodeSet.equals(nodeSet));
    }
}
