/**
 * Student: [Your Name] | Student ID: [Your ID]
 *
 * DirectedGraph.java
 * ------------------
 * Adjacency-list representation of a directed graph optimised for the
 * sink-elimination algorithm.
 *
 * Data Structure Choice:
 *  - outEdges  : HashMap<Integer, HashSet<Integer>>
 *      Maps each vertex to the set of vertices it points TO (successors).
 *      HashSet gives O(1) average add / remove / contains.
 *
 *  - inEdges   : HashMap<Integer, HashSet<Integer>>
 *      Maps each vertex to its predecessors (in-neighbours).
 *      Maintained in parallel so when a sink is removed we instantly
 *      know which predecessors may become new sinks.
 *
 *  - outDegree : HashMap<Integer, Integer>
 *      Cached out-degree per vertex for O(1) sink detection.
 *
 * Space complexity: O(V + E)
 */

import java.util.*;

public class DirectedGraph {

    private final Map<Integer, Set<Integer>> outEdges;
    private final Map<Integer, Set<Integer>> inEdges;
    private final Map<Integer, Integer>      outDegree;

    public DirectedGraph() {
        outEdges  = new HashMap<>();
        inEdges   = new HashMap<>();
        outDegree = new HashMap<>();
    }

    // -----------------------------------------------------------------------
    // Vertex
    // -----------------------------------------------------------------------

    /** Registers a vertex if not already present. O(1) amortised. */
    public void addVertex(int v) {
        outEdges .computeIfAbsent(v, k -> new HashSet<>());
        inEdges  .computeIfAbsent(v, k -> new HashSet<>());
        outDegree.putIfAbsent(v, 0);
    }

    // -----------------------------------------------------------------------
    // Edge
    // -----------------------------------------------------------------------

    /** Adds a directed edge from → to. Both endpoints auto-registered. O(1). */
    public void addEdge(int from, int to) {
        addVertex(from);
        addVertex(to);
        if (outEdges.get(from).add(to)) {           // false if duplicate
            inEdges.get(to).add(from);
            outDegree.merge(from, 1, Integer::sum);  // outDegree[from]++
        }
    }

    // -----------------------------------------------------------------------
    // Sink operations
    // -----------------------------------------------------------------------

    /**
     * Returns any sink vertex (out-degree == 0), or -1 if none exists.
     * Time complexity: O(V) — linear scan of degree map.
     */
    public int findSink() {
        for (Map.Entry<Integer, Integer> e : outDegree.entrySet()) {
            if (e.getValue() == 0) return e.getKey();
        }
        return -1;
    }

    /**
     * Removes vertex v and all edges incident to it.
     * Returns the set of predecessors whose out-degree just decreased
     * (caller uses this to detect new sinks without a full scan).
     *
     * Time complexity: O(in-degree(v) + out-degree(v))
     */
    public Set<Integer> removeVertex(int v) {
        // Remove outgoing edges v → w
        for (int w : outEdges.getOrDefault(v, Collections.emptySet())) {
            inEdges.getOrDefault(w, Collections.emptySet()).remove(v);
        }

        // Remove incoming edges u → v; collect predecessors
        Set<Integer> predecessors = new HashSet<>(
                inEdges.getOrDefault(v, Collections.emptySet()));
        for (int u : predecessors) {
            if (outEdges.getOrDefault(u, Collections.emptySet()).remove(v)) {
                outDegree.merge(u, -1, Integer::sum); // outDegree[u]--
            }
        }

        outEdges .remove(v);
        inEdges  .remove(v);
        outDegree.remove(v);

        return predecessors;
    }

    // -----------------------------------------------------------------------
    // Queries
    // -----------------------------------------------------------------------

    public boolean      isEmpty()           { return outEdges.isEmpty(); }
    public int          vertexCount()       { return outEdges.size(); }
    public Set<Integer> getVertices()       { return Collections.unmodifiableSet(outEdges.keySet()); }
    public int          getOutDegree(int v) { return outDegree.getOrDefault(v, -1); }

    public Set<Integer> getPredecessors(int v) {
        return Collections.unmodifiableSet(inEdges.getOrDefault(v, Collections.emptySet()));
    }
    public Set<Integer> getSuccessors(int v) {
        return Collections.unmodifiableSet(outEdges.getOrDefault(v, Collections.emptySet()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph (").append(vertexCount()).append(" vertices):\n");
        List<Integer> sorted = new ArrayList<>(outEdges.keySet());
        Collections.sort(sorted);
        for (int v : sorted) {
            List<Integer> succs = new ArrayList<>(outEdges.get(v));
            Collections.sort(succs);
            sb.append("  ").append(v).append(" -> ").append(succs).append('\n');
        }
        return sb.toString();
    }
}