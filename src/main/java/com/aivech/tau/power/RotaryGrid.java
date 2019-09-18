package com.aivech.tau.power;

import com.aivech.tau.Tau;
import com.google.common.collect.HashMultimap;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RotaryGrid extends Thread {
    protected static final HashMap<Identifier, ConcurrentLinkedQueue<GridUpdate>> inputQueues = new HashMap<>();
    // public static final HashMap<Identifier, ConcurrentLinkedQueue<RotaryUpdate>> outputQueues = new HashMap<>();
    private static final HashMap<Identifier,RotaryGrid> grids = new HashMap<>();

    private final MutableGraph<RotaryNode> graph;
    private final Identifier id;
    private final ConcurrentLinkedQueue<GridUpdate> queue = new ConcurrentLinkedQueue<>();


    private final HashMultimap<BlockPos,RotaryNode> nodes = new HashMultimap<>();
    private final HashSet<RotaryNode> sources = new HashSet<>();
    private final HashSet<RotaryNode> sinks = new HashSet<>();

    protected final Object lock = new Object();

    public RotaryGrid(Identifier dimension) {
        graph = GraphBuilder.undirected().allowsSelfLoops(false).build();
        grids.put(dimension,this);
        this.id = dimension;
        inputQueues.put(dimension, this.queue);
    }

    @Override
    public void run() {
        try {
            synchronized(lock) {
                while(!Thread.interrupted()) {
                    lock.wait();
                    while(this.queue.peek() != null) {
                        GridUpdate update = queue.poll();
                        switch(update.action) {
                            case ADD: break;
                            case DEL: break;
                            case UPDATE: break;
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            Tau.Log.debug("Stopping thread for "+ this.id.toString());
            inputQueues.remove(id);
            grids.remove(id);
        }

    }

    public static void addNode(IRotaryBlock block, World world, BlockPos blockPos, Direction orient, Direction[] connectsTo) {
        RotaryGrid grid = grids.get(DimensionType.getId(world.getDimension().getType()));
        RotaryNode.NodeType type;
        if(block instanceof IRotaryUser) {
            type = RotaryNode.NodeType.SINK;
        } else if(block instanceof IRotaryProvider) {
            type = RotaryNode.NodeType.SOURCE;
        } else {
            type = RotaryNode.NodeType.PATH;
        }

        RotaryNode node = new RotaryNode(RotaryNode.NodeType.SINK,blockPos, orient,connectsTo);
        grid.graph.addNode(node);
        grid.nodes.put(blockPos,node);
        switch(type) {
            case SINK : grid.sinks.add(node); break;
            case SOURCE : grid.sources.add(node); break;
            case PATH : break;
        }
    }

}