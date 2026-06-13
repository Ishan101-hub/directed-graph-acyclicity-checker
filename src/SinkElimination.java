/**
 * Student: Ishan Udawatte | Student ID: 20232686 / w2120028
 *
 * SinkElimination.java
 * --------------------
 * Implements the sink-elimination algorithm to decide whether a directed
 * graph is acyclic (a DAG).
 *
 * Algorithm:
 *   1. Collect all current sinks (out-degree == 0) into a queue.
 *   2. While the queue is non-empty:
 *        a. Dequeue sink s and remove it from the graph.
 *        b. For every predecessor u of s: if u's out-degree is now 0,
 *           enqueue u as a new sink.
 *   3. If the graph is empty  → ACYCLIC.
 *      If vertices remain     → CYCLIC (they form cyclic components).
 *
 * Time complexity : O(V + E)  — each vertex/edge processed at most once.
 * Space complexity: O(V)      — for the sink queue.
 *
 * The algorithm works on a deep copy of the graph so the original is
 * preserved for subsequent cycle detection.
 */

import java.util.*;

public class SinkElimination {

    // -----------------------------------------------------------------------
    // Result
    // -----------------------------------------------------------------------

    public static class Result {
        public final boolean     isAcyclic;
        public final List<Integer> sinksRemoved;
        public final Set<Integer>  remainingVertices;

        Result(boolean isAcyclic, List<Integer> sinksRemoved, Set<Integer> remaining) {
            this.isAcyclic         = isAcyclic;
            this.sinksRemoved      = Collections.unmodifiableList(sinksRemoved);
            this.remainingVertices = Collections.unmodifiableSet(remaining);
        }
    }

    // -----------------------------------------------------------------------
    // Main entry point
    // -----------------------------------------------------------------------

    /**
     * Runs the sink-elimination algorithm on originalGraph (not modified).
     * Prints step-by-step progress to stdout.
     */
    public static Result run(DirectedGraph originalGraph) {

        // Work on a deep copy
        DirectedGraph g = deepCopy(originalGraph);

        List<Integer> sinksRemoved = new ArrayList<>();
        Queue<Integer> sinkQueue   = new LinkedList<>();
        Set<Integer>   inQueue     = new HashSet<>();

        // Seed queue with all initial sinks
        for (int v : g.getVertices()) {
            if (g.getOutDegree(v) == 0) {
                sinkQueue.add(v);
                inQueue.add(v);
            }
        }

        System.out.println("\n=== Sink Elimination Algorithm ===");
        System.out.printf("Initial graph: %d vertices%n%n", g.vertexCount());

        int step = 1;

        while (!sinkQueue.isEmpty()) {
            int sink = sinkQueue.poll();
            inQueue.remove(sink);

            if (g.getOutDegree(sink) == -1) continue; // already removed (safety guard)

            System.out.printf("  Step %d: Remove sink %d%n", step++, sink);
            sinksRemoved.add(sink);

            Set<Integer> predecessors = g.removeVertex(sink);

            for (int pred : predecessors) {
                if (g.getOutDegree(pred) == 0 && !inQueue.contains(pred)) {
                    sinkQueue.add(pred);
                    inQueue.add(pred);
                    System.out.printf("           -> vertex %d is now a new sink%n", pred);
                }
            }
        }

        boolean      acyclic   = g.isEmpty();
        Set<Integer> remaining = new HashSet<>(g.getVertices());

        System.out.println();
        if (acyclic) {
            System.out.println("Result: ACYCLIC (yes) – all vertices eliminated as sinks.");
        } else {
            System.out.println("Result: CYCLIC (no)  – " + remaining.size()
                    + " vertices remain with no sink: " + remaining);
        }

        return new Result(acyclic, sinksRemoved, remaining);
    }

    // -----------------------------------------------------------------------
    // Deep copy helper — O(V + E)
    // -----------------------------------------------------------------------

    private static DirectedGraph deepCopy(DirectedGraph original) {
        DirectedGraph copy = new DirectedGraph();
        for (int v : original.getVertices()) {
            copy.addVertex(v);
            for (int w : original.getSuccessors(v)) {
                copy.addEdge(v, w);
            }
        }
        return copy;
    }
}