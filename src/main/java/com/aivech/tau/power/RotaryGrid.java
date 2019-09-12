package com.aivech.tau.power;

import com.aivech.tau.Tau;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;

public class RotaryGrid extends Thread {
    private static final HashMap<Identifier,RotaryGrid> grids = new HashMap<>();

    private MutableGraph<RotaryNode> graph;

    public RotaryGrid(Identifier dimension) {
        graph = GraphBuilder.undirected().allowsSelfLoops(false).build();
        grids.put(dimension,this);
    }

    @Override
    public void run() {

    }

    public static void addNode(IRotaryBlock block, World world, BlockPos blockPos) {
        DimensionType.getId(world.getDimension().getType());
    }

}