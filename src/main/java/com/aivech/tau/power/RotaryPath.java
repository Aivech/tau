package com.aivech.tau.power;

import java.util.LinkedHashSet;

public class RotaryPath {
    final RotaryNode.Source source;
    final LinkedHashSet<RotaryNode> nodeSet = new LinkedHashSet<>();
    private RotaryNode lastNode;

    RotaryPath(RotaryNode.Source n) {
        source = n;
        lastNode = n;
    }

    RotaryPath copy() {
        RotaryPath copy = new RotaryPath(this.source);
        for(RotaryNode node : nodeSet) {
            copy.append(node);
        }
        return copy;
    }

    void append(RotaryNode n) {
        lastNode = n;
        nodeSet.add(n);
    }

    boolean contains(RotaryNode n) {
        return nodeSet.contains(n);
    }

    RotaryNode getLast() {
        return lastNode;
    }

    @Override
    public int hashCode() {
        return source.hashCode() ^ lastNode.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof RotaryPath && ((RotaryPath)o).nodeSet.equals(nodeSet));
    }
}
