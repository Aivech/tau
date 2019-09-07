package com.aivech.tau.power;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

public class RotaryGrid {
    MutableGraph<RotaryNode> grid = GraphBuilder.directed().allowsSelfLoops(false).build();

    public RotaryGrid() {

    }
}