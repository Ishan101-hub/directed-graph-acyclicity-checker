/**
 * Student: [Your Name] | Student ID: [Your ID]
 *
 * Main.java
 * ---------
 * Entry point for the Acyclicity Checker program.
 *
 * Usage
 * -----
 * java Main <input-file>
 *
 * e.g.   java Main cyclic.txt
 * java Main acyclic.txt
 *
 * The program:
 * 1. Parses the graph from the given file.
 * 2. Prints the loaded graph.
 * 3. Runs the sink-elimination algorithm, printing every step, and times it.
 * 4. Reports whether the graph is acyclic or cyclic, along with execution time.
 * 5. If cyclic, runs DFS cycle detection and prints one concrete cycle.
 */
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // ---- Command-line argument check -----------------------------------
        if (args.length < 1) {
            System.err.println("Usage: java Main <input-file>");
            System.err.println("Example: java Main cyclic.txt");
            System.exit(1);
        }

        String filePath = args[0];

        // ---- 1. Parse the graph --------------------------------------------
        DirectedGraph graph;
        try {
            graph = GraphParser.parse(filePath);
        } catch (IOException e) {
            System.err.println("[Error] Cannot read file: " + filePath);
            System.err.println("        " + e.getMessage());
            System.exit(1);
            return; // unreachable; satisfies the compiler
        } catch (IllegalArgumentException e) {
            System.err.println("[Error] Malformed input: " + e.getMessage());
            System.exit(1);
            return;
        }

        // ---- 2. Display the graph ------------------------------------------
        System.out.println("\n" + graph);

        // ---- 3. Run sink elimination (works on an internal copy) -----------
        // Start the timer
        long startTime = System.nanoTime();

        SinkElimination.Result result = SinkElimination.run(graph);

        // Stop the timer
        long endTime = System.nanoTime();

        // Calculate duration in milliseconds (using double for precision on fast runs)
        double durationMs = (endTime - startTime) / 1_000_000.0;

        // ---- 4. Print final verdict ----------------------------------------
        System.out.println("\n==========================================");
        System.out.printf(" Execution Time: %.3f ms\n", durationMs);
        System.out.println("------------------------------------------");

        if (result.isAcyclic) {
            System.out.println(" ANSWER: YES – the graph is ACYCLIC.");
            System.out.println(" Elimination order: " + result.sinksRemoved);
        } else {
            System.out.println(" ANSWER: NO  – the graph is CYCLIC.");
            System.out.println(" Sinks removed before halting: " + result.sinksRemoved);
            System.out.println(" Remaining (cyclic) vertices:  " + result.remainingVertices);
        }
        System.out.println("==========================================\n");

        // ---- 5. Cycle detection (Task 5) -----------------------------------
        if (!result.isAcyclic) {
            List<Integer> cycle = CycleDetector.findCycle(graph);
            if (cycle.isEmpty()) {
                // Shouldn't happen if sink-elimination says cyclic, but guard
                System.out.println("[CycleDetector] Unexpected: no cycle found by DFS.");
            }
            // The cycle is already printed inside CycleDetector.findCycle()
        }
    }
}