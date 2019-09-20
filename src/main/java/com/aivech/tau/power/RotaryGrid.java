package com.aivech.tau.power;

import com.aivech.tau.Tau;
import com.aivech.tau.event.WorldLoadCallback;
import com.aivech.tau.event.WorldUnloadCallback;
import com.aivech.tau.power.RotaryNode.GridTransaction;
import com.google.common.collect.HashMultimap;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.dimension.DimensionType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RotaryGrid extends Thread {
    static final HashMap<Identifier, ConcurrentLinkedQueue<GridUpdate>> GRID_UPDATE_QUEUES = new HashMap<>();
    static final ConcurrentLinkedQueue<WorldPowerUpdate> WORLD_UPDATE_QUEUE = new ConcurrentLinkedQueue<>();
    static final ConcurrentHashMap<Identifier, Object> LOCK_OBJECTS = new ConcurrentHashMap<>();
    // public static final HashMap<Identifier, ConcurrentLinkedQueue<RotaryUpdate>> outputQueues = new HashMap<>();
    private static final HashMap<Identifier,RotaryGrid> GRIDS = new HashMap<>();

    private final MutableGraph<RotaryNode> graph;
    private final Identifier id;
    private final ConcurrentLinkedQueue<GridUpdate> changeQueue = new ConcurrentLinkedQueue<>();


    private final HashMultimap<BlockPos, RotaryNode> blockPosToNodeMap = HashMultimap.create();
    private final HashSet<RotaryNode.Source> sourceCache = new HashSet<>();
    private final HashSet<RotaryNode> sinkCache = new HashSet<>();
    private final HashMap<RotaryPath, Subgrid> subgridCache = new HashMap<>();

    private final HashSet<RotaryNode.Source> pathfindQueue = new HashSet<>();
    private final HashSet<RotaryNode> updateQueue = new HashSet<>();

    public RotaryGrid(Identifier dimension) {
        graph = GraphBuilder.undirected().allowsSelfLoops(false).build();
        this.id = dimension;
    }

    @Override
    public void run() {
        Tau.Log.debug("Starting grid worker thread for "+this.id.toString());
        Object lock = new Object();
        LOCK_OBJECTS.put(this.id, lock);
        try {
            synchronized(lock) {
                while(!Thread.interrupted()) {
                    lock.wait();

                    // Resolve changeQueue
                    while (this.changeQueue.peek() != null) {
                        GridUpdate update = changeQueue.poll();
                        switch(update.action) {
                            case ADD: add(update.node); break;
                            case DEL: remove(update.pos,update.dir); break;
                            case UPDATE:
                                update(update.pos, update.dir);
                                break;
                        }
                    }

                    // Pathfinding
                    for (RotaryNode node : pathfindQueue) {
                        pathfindQueue.remove(node);
                        if(graph.adjacentNodes(node).isEmpty()) continue;
                        Subgrid subgrid = new Subgrid(findConnected(node));
                        subgrid.solve();
                    }

                    // Calculate source to node transactions

                }
            }
        } catch (InterruptedException e) {
        }
        Tau.Log.debug("Stopping grid worker thread for "+ this.id.toString());
        LOCK_OBJECTS.remove(id);
        GRIDS.remove(id);
        GRID_UPDATE_QUEUES.remove(id);
    }

    private void add(RotaryNode node) {
        // add to graph and caches
        graph.addNode(node);
        blockPosToNodeMap.put(node.pos, node);
        switch(node.type) {
            case SINK:
                sinkCache.add(node);
                break;
            case SOURCE :
                sourceCache.add((RotaryNode.Source)node);
                pathfindQueue.add((RotaryNode.Source)node);
                break;
            default:
                break;
        }

        // connect to adjacents
        for (Direction dir : node.connects) {
            BlockPos offset = node.pos.offset(dir);
            Set<RotaryNode> connects = blockPosToNodeMap.get(offset);
            if(connects != null) {
                for(RotaryNode neighbor : connects) {
                    if(neighbor.connects.contains(dir.getOpposite())) {
                        graph.putEdge(node,neighbor);

                        this.invalidateSubgrid(node);
                    }
                }
            }
        }
    }

    private void remove(BlockPos pos, Direction dir) {
        Set<RotaryNode> removed = blockPosToNodeMap.get(pos);
        for(RotaryNode node : removed) {
            if (dir == null || node.orient == dir) {
                this.invalidateSubgrid(node);
                graph.removeNode(node);
                blockPosToNodeMap.remove(pos, node);
                sourceCache.remove(node);
                sinkCache.remove(node);
                pathfindQueue.remove(node);
            }
        }
    }

    private void update(BlockPos pos, Direction dir) {
        Set<RotaryNode> updateQueue = blockPosToNodeMap.get(pos);
        for (RotaryNode n : updateQueue) {
            if (dir == null || n.orient == dir) {
                updateQueue.add(n);
            }
        }
    }

    /* unused
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
    }*/


    private void invalidateSubgrid(RotaryNode node) {
        Subgrid subgrid = subgridCache.get(node.paths.get(0));
        for (RotaryNode n : subgrid.nodes) {
            updateQueue.add(n);
            if (n.type == RotaryNode.NodeType.SOURCE) {
                pathfindQueue.add((RotaryNode.Source)n);
                for (RotaryPath path : n.paths) {
                    subgridCache.remove(path);
                }
            }
            n.paths.clear();
        }
    }

    public static void registerHandlers() {
        WorldLoadCallback.EVENT.register((world -> {
            Identifier dimId = DimensionType.getId(world.getDimension().getType());
            RotaryGrid grid = new RotaryGrid(dimId);
            GRIDS.put(dimId,grid);
            GRID_UPDATE_QUEUES.put(dimId, grid.changeQueue);
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
                pathfindQueue.remove(n);
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

    private void performTransactions(RotaryPath path) {
        GridTransaction trans = new GridTransaction();
        for (RotaryNode n : path.nodeSet) {
            WorldPowerUpdate update = new WorldPowerUpdate(n);
            update.setInputValues(trans, path.source);
            n.handleTransaction(trans);
            update.setOutputValues(trans, path.source);
            WORLD_UPDATE_QUEUE.add(update);
        }
    }

    private class Subgrid {
        private HashSet<RotaryNode> nodes;
        private ArrayList<RotaryNode.Source> roots = new ArrayList<>();
        private ArrayList<RotaryPath> paths = new ArrayList<>();

        private Subgrid(HashSet<RotaryNode> nodes) {
            this.nodes = nodes;
            for(RotaryNode n : nodes) {
                if (sourceCache.contains(n))
                    roots.add((RotaryNode.Source)n);
            }
        }

        private void solve() {
            for (RotaryNode.Source source : roots) {
                RotaryPath first = new RotaryPath(source);
                ArrayDeque<RotaryPath> next = new ArrayDeque<>();
                next.addLast(first);
                while(!next.isEmpty()) {
                    RotaryPath path = next.pollFirst();
                    while(true) {
                        RotaryNode node = path.getLast();
                        int degree = graph.degree(node);
                        if (sinkCache.contains(node) || degree < 2) { // end of path
                            paths.add(path);
                            break;
                        }
                        Set<RotaryNode> adjacent = graph.adjacentNodes(node);
                        ArrayDeque<RotaryNode> children  = new ArrayDeque<>();
                        for(RotaryNode neighbor : adjacent) {
                            if (!path.contains(neighbor) && node.canPathTo(neighbor)) children.addLast(neighbor);
                        }
                        if(children.isEmpty()) { // loop & deadend avoidance
                            paths.add(path);
                            break;
                        }
                        path.append(children.pollFirst());

                        //create new paths at junctions
                        while(!children.isEmpty()) {
                            RotaryPath branch = path.copy();
                            branch.append(children.pollFirst());
                            next.addLast(branch);
                        }
                    }
                }
            }

            // caching
            for(RotaryPath path : paths) {
                subgridCache.put(path, this);
                for(RotaryNode node : path.nodeSet) {
                    node.paths.add(path);
                }
            }
        }
    }
}