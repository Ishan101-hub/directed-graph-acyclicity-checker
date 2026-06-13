/**
 * Student: Ishan Udawatte | Student ID: 20232686 / w2120028
 *
 * CycleDetector.java
 * ------------------
 * Finds and prints one cycle using iterative DFS with three-colour marking.
 *
 * Colour semantics:
 *   WHITE (0) – not yet visited
 *   GRAY  (1) – on the current DFS stack (active path)
 *   BLACK (2) – fully processed
 *
 * A back edge u → v exists when v is GRAY when we arrive from u.
 * That back edge closes a cycle. The concrete path is reconstructed
 * by walking the predecessor map from u back to v.
 *
 * Bug fix: The original code used `parent.put(v, v)` as a sentinel
 * before calling reconstructCycle, which overwrote v's real parent
 * and caused the path reconstruction to break or terminate early.
 * The fix passes backEdgeTail directly to reconstructCycle without
 * modifying the parent map, and builds the path with addFirst so
 * no reversal step is needed.
 *
 * Time complexity : O(V + E)
 * Space complexity: O(V)
 */

import java.util.*;

public class CycleDetector {

    private static final int WHITE = 0;
    private static final int GRAY  = 1;
    private static final int BLACK = 2;

    /**
     * Finds one cycle in graph, prints it, and returns it as a vertex list.
     * The list starts and ends with the same vertex (cycle is closed).
     * Returns an empty list if the graph is acyclic.
     */
    public static List<Integer> findCycle(DirectedGraph graph) {
        Map<Integer, Integer> colour = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();

        for (int v : graph.getVertices()) {
            colour.put(v, WHITE);
            parent.put(v, -1);
        }

        for (int start : graph.getVertices()) {
            if (colour.get(start) == WHITE) {
                List<Integer> cycle = dfs(start, graph, colour, parent);
                if (!cycle.isEmpty()) {
                    printCycle(cycle);
                    return cycle;
                }
            }
        }
        return Collections.emptyList();
    }

    // -----------------------------------------------------------------------
    // Iterative DFS (avoids stack overflow on large graphs)
    // -----------------------------------------------------------------------

    private static List<Integer> dfs(int start,
                                     DirectedGraph graph,
                                     Map<Integer, Integer> colour,
                                     Map<Integer, Integer> parent) {

        // Stack frames: [vertex, Iterator<Integer> over its successors]
        Deque<Object[]> stack = new ArrayDeque<>();
        colour.put(start, GRAY);
        stack.push(new Object[]{start,
                new ArrayList<>(graph.getSuccessors(start)).iterator()});

        while (!stack.isEmpty()) {
            Object[] frame = stack.peek();
            int v = (int) frame[0];
            @SuppressWarnings("unchecked")
            Iterator<Integer> it = (Iterator<Integer>) frame[1];

            if (it.hasNext()) {
                int w = it.next();

                if (colour.get(w) == GRAY) {
                    // Back edge v → w: cycle found.
                    // Pass v (backEdgeTail) and w (cycleStart) directly —
                    // do NOT modify parent map here (that was the bug).
                    return reconstructCycle(w, v, parent);
                }
                if (colour.get(w) == WHITE) {
                    colour.put(w, GRAY);
                    parent.put(w, v);
                    stack.push(new Object[]{w,
                            new ArrayList<>(graph.getSuccessors(w)).iterator()});
                }
                // BLACK: already fully explored — skip

            } else {
                colour.put(v, BLACK);
                stack.pop();
            }
        }
        return Collections.emptyList();
    }

    // -----------------------------------------------------------------------
    // Reconstruct cycle path from predecessor map
    //
    // cycleStart   – the GRAY vertex w that the back edge points to
    // backEdgeTail – the vertex v from which the back edge originates
    //
    // We walk parent pointers from backEdgeTail back to cycleStart,
    // prepending each node so the list is already in forward order.
    // Then we append cycleStart again to close the cycle visually.
    // No reversal needed.
    // -----------------------------------------------------------------------

    private static List<Integer> reconstructCycle(int cycleStart,
                                                  int backEdgeTail,
                                                  Map<Integer, Integer> parent) {
        LinkedList<Integer> path = new LinkedList<>();

        int cur = backEdgeTail;
        while (cur != cycleStart) {
            path.addFirst(cur);
            int p = parent.getOrDefault(cur, -1);
            if (p == -1) break; // safety guard — should not happen in a valid cycle
            cur = p;
        }
        path.addFirst(cycleStart); // start of cycle
        path.addLast(cycleStart);  // close the cycle

        return new ArrayList<>(path);
    }

    // -----------------------------------------------------------------------
    // Output
    // -----------------------------------------------------------------------

    private static void printCycle(List<Integer> cycle) {
        System.out.println("\n=== Cycle Found ===");
        System.out.print("Cycle path: ");
        for (int i = 0; i < cycle.size(); i++) {
            System.out.print(cycle.get(i));
            if (i < cycle.size() - 1) System.out.print(" -> ");
        }
        System.out.println();
    }
}