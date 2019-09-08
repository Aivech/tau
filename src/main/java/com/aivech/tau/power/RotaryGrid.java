package com.aivech.tau.power;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class RotaryGrid extends Thread {
    private static HashMap<Identifier,RotaryGrid> grids = new HashMap<>();

    private MutableGraph<RotaryNode> grid;

    public RotaryGrid() {
        grid = GraphBuilder.directed().allowsSelfLoops(false).build();
    }

    @Override
    public void run() {

    }

}