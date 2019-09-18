package com.aivech.tau.power;

import com.aivech.tau.Tau;
import com.aivech.tau.event.WorldLoadCallback;
import com.aivech.tau.event.WorldUnloadCallback;
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
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RotaryGrid extends Thread {
    protected static final HashMap<Identifier, ConcurrentLinkedQueue<GridUpdate>> inputQueues = new HashMap<>();
    // public static final HashMap<Identifier, ConcurrentLinkedQueue<RotaryUpdate>> outputQueues = new HashMap<>();
    private static final HashMap<Identifier,RotaryGrid> grids = new HashMap<>();

    private final MutableGraph<RotaryNode> graph;
    private final Identifier id;
    private final ConcurrentLinkedQueue<GridUpdate> queue = new ConcurrentLinkedQueue<>();


    private final HashMultimap<BlockPos,RotaryNode> nodes = HashMultimap.create();
    private final HashSet<RotaryNode> sources = new HashSet<>();
    private final HashSet<RotaryNode> sinks = new HashSet<>();

    private final HashSet<RotaryNode> pathfind = new HashSet<>();

    protected final Object lock = new Object();

    public RotaryGrid(Identifier dimension) {
        graph = GraphBuilder.undirected().allowsSelfLoops(false).build();
        this.id = dimension;
    }

    @Override
    public void run() {
        Tau.Log.debug("Starting grid worker thread for "+this.id.toString());
        try {
            synchronized(lock) {
                while(!Thread.interrupted()) {
                    lock.wait();
                    while(this.queue.peek() != null) {
                        GridUpdate update = queue.poll();
                        switch(update.action) {
                            case ADD: add(((GridUpdate.Add)update).node); break;
                            case DEL: break;
                            case UPDATE: break;
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
        } catch (ClassCastException e) {
            Tau.Log.fatal("Invalid operation performed on the grid. If this happens, it means someone needs to stop abusing the API.");
            Tau.Log.fatal("https://youtu.be/Zb67FzEmEcY?t=3");
            throw(e);
        }
        Tau.Log.debug("Stopping grid worker thread for "+ this.id.toString());
        grids.remove(id);
        inputQueues.remove(id);
    }

    private void add(RotaryNode node) {
        // add to graph and caches
        graph.addNode(node);
        nodes.put(node.pos, node);
        switch(node.type) {
            case SINK : sinks.add(node); break;
            case SOURCE : sources.add(node); break;
            case PATH : break;
        }

        // connect to adjacents
        for (Direction dir : node.connects) {
            BlockPos offset = node.pos.offset(dir);
            Set<RotaryNode> connects = nodes.get(offset);
            if(connects != null) {
                for(RotaryNode neighbor : connects) {
                    if(neighbor.connects.contains(dir.getOpposite())) {
                        graph.putEdge(node,neighbor);
                        neighbor.invalidatePaths();
                    }
                }
            }
        }
    }

    public static void registerHandlers() {
        WorldLoadCallback.EVENT.register((world -> {
            Identifier dimId = DimensionType.getId(world.getDimension().getType());
            RotaryGrid grid = new RotaryGrid(dimId);
            grids.put(dimId,grid);
            inputQueues.put(dimId,grid.queue);
            grid.start();
        }));
        WorldUnloadCallback.EVENT.register((world -> {
            Identifier dimId = DimensionType.getId(world.getDimension().getType());
            grids.get(dimId).interrupt();
        }));
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

    }

}