/**
 * Student: Ishan Udawatte | Student ID: 20232686 / w2120028
 *
 * GraphParser.java
 * ----------------
 * Reads a directed graph from a plain-text file.
 *
 * File format — one edge per line, two integers separated by whitespace:
 *   1 2
 *   3 1
 *   2 5
 *
 * Blank lines and lines starting with '#' are ignored.
 * Time complexity: O(E)
 */

import java.io.*;

public class GraphParser {

    /**
     * Parses the file at filePath and returns the resulting DirectedGraph.
     *
     * @throws IOException              if the file cannot be read
     * @throws IllegalArgumentException if a line has an unexpected format
     */
    public static DirectedGraph parse(String filePath) throws IOException {
        DirectedGraph graph = new DirectedGraph();
        int edgeCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");

                // --- NEW CODE: Skip the vertex count header ---
                if (parts.length == 1) {
                    continue;
                }

                if (parts.length != 2) {
                    throw new IllegalArgumentException(
                            "Line " + lineNumber + ": expected two integers, got: \"" + line + "\"");
                }

                try {
                    int from = Integer.parseInt(parts[0]);
                    int to   = Integer.parseInt(parts[1]);
                    graph.addEdge(from, to);
                    edgeCount++;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Line " + lineNumber + ": non-integer value in \"" + line + "\"");
                }
            }
        }

        System.out.println("[Parser] Loaded " + graph.vertexCount()
                + " vertices and " + edgeCount + " edges from \"" + filePath + "\".");
        return graph;
    }
}