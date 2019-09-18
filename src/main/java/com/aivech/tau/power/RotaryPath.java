package com.aivech.tau.power;

import java.util.ArrayList;

public class RotaryPath {
    public ArrayList<RotaryNode> nodes;


    @Override
    public int hashCode() {
        return nodes.get(0).hashCode() ^ nodes.get(nodes.size()-1).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RotaryPath && ((RotaryPath)o).nodes.equals(nodes);
    }
}
