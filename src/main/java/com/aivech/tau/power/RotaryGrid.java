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

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RotaryGrid extends Thread {
    static final HashMap<Identifier, ConcurrentLinkedQueue<GridUpdate>> UPDATE_QUEUES = new HashMap<>();
    // public static final HashMap<Identifier, ConcurrentLinkedQueue<RotaryUpdate>> outputQueues = new HashMap<>();
    private static final HashMap<Identifier,RotaryGrid> GRIDS = new HashMap<>();

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
                            case ADD: add(update.node); break;
                            case DEL: remove(update.pos,update.dir); break;
                            case UPDATE: break;
                        }
                    }

                    ArrayList<MutableGraph<RotaryNode>> subgraphs = new ArrayList<>();
                    for(RotaryNode node : pathfind) {
                        pathfind.remove(node);
                        subgraphs.add(findSubgrid(node));
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
        GRIDS.remove(id);
        UPDATE_QUEUES.remove(id);
    }

    private void add(RotaryNode node) {
        // add to graph and caches
        graph.addNode(node);
        nodes.put(node.pos, node);
        switch(node.type) {
            case SINK : sinks.add(node); break;
            case SOURCE :
                sources.add(node);
                pathfind.add(node);
                break;
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

                        this.invalidatePaths(node);
                    }
                }
            }
        }
    }

    private void remove(BlockPos pos, Direction dir) {
        Set<RotaryNode> removed = nodes.get(pos);
        for(RotaryNode node : removed) {
            if(dir == null || node.dir == dir){
                this.invalidatePaths(node);
                graph.removeNode(node);
                nodes.remove(pos,node);
                sources.remove(node);
                sinks.remove(node);
                pathfind.remove(node);
            }
        }
    }

    private MutableGraph<RotaryNode> findSubgrid(RotaryNode node) {
        MutableGraph<RotaryNode> subgraph = GraphBuilder.undirected().allowsSelfLoops(false).build();
        HashSet<RotaryNode> visited = findConnected(node);
        for (RotaryNode n : visited) {
            subgraph.addNode(n);
            graph.adjacentNodes(n).forEach((child -> {
                subgraph.putEdge(n,child);
            }));
        }

        return subgraph;
    }



    private void invalidatePaths(RotaryNode node) {
        for(RotaryPath path : node.paths) {
            pathfind.add(path.nodes.get(0));
        }
        node.paths.clear();
    }

    public static void registerHandlers() {
        WorldLoadCallback.EVENT.register((world -> {
            Identifier dimId = DimensionType.getId(world.getDimension().getType());
            RotaryGrid grid = new RotaryGrid(dimId);
            GRIDS.put(dimId,grid);
            UPDATE_QUEUES.put(dimId,grid.queue);
            grid.start();
        }));
        WorldUnloadCallback.EVENT.register((world -> {
            Identifier dimId = DimensionType.getId(world.getDimension().getType());
            GRIDS.get(dimId).interrupt();
        }));
    }

    private HashSet<RotaryNode> findConnected(RotaryNode start) {
        HashSet<RotaryNode> visited = new HashSet<>();
        ArrayDeque<RotaryNode> cur = new ArrayDeque<>(graph.adjacentNodes(start));
        ArrayDeque<RotaryNode> next = new ArrayDeque<>();
        visited.add(start);
        while(true) {
            while(!cur.isEmpty()) {
                RotaryNode n = cur.removeLast();
                visited.add(n);
                pathfind.remove(n);
                for(RotaryNode child : graph.adjacentNodes(n)) {
                    if(!visited.contains(child)) {next.push(child);}
                }
            }
            if(next.isEmpty()) { break; }
            ArrayDeque<RotaryNode> tmp = cur;
            cur = next;
            next = tmp;
        }
        return visited;
    }
}