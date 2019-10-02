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
    static final ConcurrentHashMap<Identifier, RotaryGrid> GRIDS = new ConcurrentHashMap<>();

    final Object lock;
    final ConcurrentLinkedQueue<GridUpdate> changeQueue = new ConcurrentLinkedQueue<>();

    private final MutableGraph<RotaryNode> graph;
    private final Identifier id;

    private final HashMultimap<BlockPos, RotaryNode> blockPosToNodeMap = HashMultimap.create();
    private final HashSet<RotaryNode.Source> sourceCache = new HashSet<>();
    private final HashSet<RotaryNode> sinkCache = new HashSet<>();
    private final HashMap<RotaryPath, Subgrid> subgridCache = new HashMap<>();

    private final HashSet<RotaryNode.Source> pathfindQueue = new HashSet<>();
    private final ArrayDeque<RotaryNode> nodeUpdateQueue = new ArrayDeque<>();
    private final ArrayDeque<RotaryNode> worldUpdateQueue = new ArrayDeque<>();

    private RotaryGrid(Identifier dimension) {
        this.graph = GraphBuilder.undirected().allowsSelfLoops(false).build();
        this.id = dimension;
        this.lock = new Object();
        this.setName(Tau.MODID + "worker:" + id.getPath());
    }

    @Override
    public void run() {
        Tau.Log.debug("Starting grid worker thread for " + this.id.toString());
        try {
            synchronized (lock) {
                while (! Thread.interrupted()) {
                    lock.wait();
                    if (this.changeQueue.isEmpty()) continue;

                    // Resolve changeQueue
                    while (this.changeQueue.peek() != null) {
                        GridUpdate update = changeQueue.poll();
                        switch (update.action) {
                            case ADD:
                                add(update.node);
                                break;
                            case DEL:
                                remove(update.pos, update.dir);
                                break;
                            case UPDATE:
                                update(update.pos, update.dir);
                                break;
                        }
                    }

                    // Pathfinding calc
                    ArrayList<Subgrid> subgrids = new ArrayList<>();
                    for (RotaryNode.Source node : pathfindQueue) {
                        pathfindQueue.remove(node);
                        if (graph.adjacentNodes(node).isEmpty()) continue;
                        Subgrid subgrid = new Subgrid(findConnected(node));
                        subgrid.solve();
                        subgrids.add(subgrid);
                    }

                    // Resolve updates and calculate source to node transactions
                    while (! nodeUpdateQueue.isEmpty()) {
                        RotaryNode n = nodeUpdateQueue.pollFirst();
                        if (! graph.nodes().contains(n)) continue;
                        n.updateNode();
                        for (RotaryPath path : n.paths) {
                            performTransactions(path);
                        }
                    }
                    for (Subgrid g : subgrids) {
                        for (RotaryPath p : g.paths) {
                            performTransactions(p);
                        }
                    }

                    // Push solved values to world
                    while (! worldUpdateQueue.isEmpty()) {
                        RotaryNode n = worldUpdateQueue.pollFirst();
                        if (! graph.nodes().contains(n)) continue;
                        n.updateBlock();
                    }
                }
            }
        } catch (InterruptedException e) {
        }
        Tau.Log.debug("Stopping grid worker thread for " + this.id.toString());
        GRIDS.remove(id);
    }

    private void add(RotaryNode node) {
        // add to graph and caches
        graph.addNode(node);
        blockPosToNodeMap.put(node.pos, node);
        switch (node.type) {
            case SINK:
                sinkCache.add(node);
                break;
            case SOURCE:
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
            if (connects != null) {
                for (RotaryNode neighbor : connects) {
                    if (neighbor.connects.contains(dir.getOpposite())) {
                        graph.putEdge(node, neighbor);

                        this.invalidateSubgrid(node);
                    }
                }
            }
        }
    }

    private void remove(BlockPos pos, Direction dir) {
        Set<RotaryNode> removed = blockPosToNodeMap.get(pos);
        for (RotaryNode node : removed) {
            if (dir == null || node.orient == dir) {

                this.invalidateSubgrid(node);
                graph.removeNode(node);
                blockPosToNodeMap.remove(pos, node);

                switch (node.type) {
                    case SOURCE: {
                        sourceCache.remove(node);
                        pathfindQueue.remove(node);
                        break;
                    }
                    case SINK: {
                        sinkCache.remove(node);
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }

    private void update(BlockPos pos, Direction dir) {
        Set<RotaryNode> updateQueue = blockPosToNodeMap.get(pos);
        for (RotaryNode n : updateQueue) {
            if (dir == null || n.orient == dir) {
                nodeUpdateQueue.addLast(n);
            }
        }
    }

    private void invalidateSubgrid(RotaryNode node) {
        if (node.paths.size() == 0) return;
        Subgrid subgrid = subgridCache.get(node.paths.get(0));
        for (RotaryNode n : subgrid.nodes) {
            worldUpdateQueue.add(n);
            if (n.type == RotaryNode.NodeType.SOURCE) {
                pathfindQueue.add((RotaryNode.Source)n);
                for (RotaryPath path : n.paths) {
                    subgridCache.remove(path);
                }
            }
            n.paths.clear();
        }
    }

    private HashSet<RotaryNode> findConnected(RotaryNode start) {
        HashSet<RotaryNode> visited = new HashSet<>();
        ArrayDeque<RotaryNode> cur = new ArrayDeque<>(graph.adjacentNodes(start));
        ArrayDeque<RotaryNode> next = new ArrayDeque<>();
        visited.add(start);
        while (true) {
            while (! cur.isEmpty()) {
                RotaryNode n = cur.removeLast();
                visited.add(n);
                if (n instanceof RotaryNode.Source)
                    pathfindQueue.remove(n);
                for (RotaryNode child : graph.adjacentNodes(n)) {
                    if (! visited.contains(child)) {
                        next.push(child);
                    }
                }
            }
            if (next.isEmpty()) {
                break;
            }
            ArrayDeque<RotaryNode> tmp = cur;
            cur = next;
            next = tmp;
        }
        return visited;
    }

    private void performTransactions(RotaryPath path) {
        GridTransaction trans = new GridTransaction();
        for (RotaryNode n : path.nodeSet) {
            n.handleTransaction(trans, path);
            worldUpdateQueue.add(n);
        }
    }

    private class Subgrid {
        private HashSet<RotaryNode> nodes;
        private ArrayList<RotaryNode.Source> roots = new ArrayList<>();
        private ArrayList<RotaryPath> paths = new ArrayList<>();

        private Subgrid(HashSet<RotaryNode> nodes) {
            this.nodes = nodes;
            for (RotaryNode n : nodes) {
                if (n instanceof RotaryNode.Source && sourceCache.contains(n))
                    roots.add((RotaryNode.Source)n);
            }
        }

        private void solve() {
            for (RotaryNode.Source source : roots) {
                RotaryPath first = new RotaryPath(source);
                ArrayDeque<RotaryPath> next = new ArrayDeque<>();
                next.addLast(first);
                while (! next.isEmpty()) {
                    RotaryPath path = next.pollFirst();
                    while (true) {
                        RotaryNode node = path.getLast();
                        int degree = graph.degree(node);
                        if (sinkCache.contains(node) || degree < 2) { // end of path
                            paths.add(path);
                            break;
                        }
                        Set<RotaryNode> adjacent = graph.adjacentNodes(node);
                        ArrayDeque<RotaryNode> children = new ArrayDeque<>();
                        for (RotaryNode neighbor : adjacent) {
                            if (! path.contains(neighbor) && neighbor.canReceivePowerFrom(node))
                                children.addLast(neighbor);
                        }
                        if (children.isEmpty()) { // loop & deadend avoidance
                            paths.add(path);
                            break;
                        }
                        path.append(children.pollFirst());

                        //create new paths at junctions
                        while (! children.isEmpty()) {
                            RotaryPath branch = path.copy();
                            branch.append(children.pollFirst());
                            next.addLast(branch);
                        }
                    }
                }
            }

            // caching
            for (RotaryPath path : paths) {
                subgridCache.put(path, this);
                for (RotaryNode node : path.nodeSet) {
                    node.paths.add(path);
                }
            }
        }
    }

    public static void registerHandlers() {
        WorldLoadCallback.EVENT.register((world -> {
            Identifier dimId = DimensionType.getId(world.getDimension().getType());
            RotaryGrid grid = new RotaryGrid(dimId);
            GRIDS.put(dimId, grid);
            grid.start();
        }));
        WorldUnloadCallback.EVENT.register((world -> {
            Identifier dimId = DimensionType.getId(world.getDimension().getType());
            GRIDS.get(dimId).interrupt();
        }));
    }
}