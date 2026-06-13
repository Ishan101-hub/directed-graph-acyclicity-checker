# Directed Graph Acyclicity Checker

A Java implementation of two classical graph algorithms — **sink elimination** and **iterative DFS cycle detection** — to determine whether a directed graph is acyclic (a DAG) or cyclic, and if cyclic, to find and print one concrete cycle path.

Built as coursework for *Algorithms: Theory, Design, and Implementation (5SENG003W)* at the University of Westminster / Informatics Institute of Technology.

---

## Table of Contents

- [Overview](#overview)
- [Algorithms](#algorithms)
  - [Algorithm 1 — Sink Elimination](#algorithm-1--sink-elimination)
  - [Algorithm 2 — Iterative DFS Cycle Detection](#algorithm-2--iterative-dfs-cycle-detection)
- [Data Structure Design](#data-structure-design)
- [Complexity Analysis](#complexity-analysis)
- [Project Structure](#project-structure)
- [How to Run](#how-to-run)
- [Input File Format](#input-file-format)
- [Sample Output](#sample-output)
- [Benchmark Results](#benchmark-results)

---

## Overview

Given a directed graph in a plain-text edge-list file, the program:

1. Parses the graph into an adjacency-list representation.
2. Runs **sink elimination** to decide acyclicity in **O(V + E)** time.
3. If cyclic, runs **iterative DFS** with three-colour marking to find and print one concrete cycle path, also in **O(V + E)** time.

---

## Algorithms

### Algorithm 1 — Sink Elimination

A *sink* is any vertex with out-degree zero. Every DAG must contain at least one sink. The algorithm exploits this:

```
1. Collect all current sinks into a queue.
2. While the queue is non-empty:
     a. Dequeue a sink s and remove it from the graph.
     b. For every predecessor u of s:
          if u's out-degree is now 0, enqueue u as a new sink.
3. If the graph is empty  →  ACYCLIC
   If vertices remain    →  CYCLIC  (they form cyclic components)
```

The algorithm works on a **deep copy** of the graph so the original is preserved for subsequent cycle detection.

### Algorithm 2 — Iterative DFS Cycle Detection

Uses a three-colour marking scheme to detect back edges, which indicate cycles:

| Colour | Value | Meaning |
|--------|-------|---------|
| WHITE  | 0     | Not yet visited |
| GRAY   | 1     | On the current DFS stack (active path) |
| BLACK  | 2     | Fully explored |

A back edge `u → v` exists when `v` is **GRAY** at the time of traversal from `u`. The cycle path is reconstructed by walking the predecessor map from `u` back to `v` using `addFirst()`, producing a forward-ordered path without any reversal step.

The DFS is implemented **iteratively** using an explicit `Deque` to avoid `StackOverflowError` on large inputs.

> **Bug fixed:** The original skeleton code called `parent.put(v, v)` before `reconstructCycle()`, which overwrote `v`'s real parent entry and caused path reconstruction to terminate immediately at `v`, producing a wrong or empty cycle. The fix passes `backEdgeTail` and `cycleStart` directly as parameters without touching the parent map.

---

## Data Structure Design

`DirectedGraph` is backed by three `HashMap`s:

| Map | Purpose | Benefit |
|-----|---------|---------|
| `outEdges` | `vertex → Set<successors>` | O(1) edge lookup / traversal |
| `inEdges`  | `vertex → Set<predecessors>` | O(1) predecessor lookup on sink removal |
| `outDegree`| `vertex → int` | O(1) sink detection, O(1) degree update |

The `inEdges` map is the key design decision: when a sink is removed, its predecessors are found instantly rather than by scanning all edges, keeping the overall complexity linear.

---

## Complexity Analysis

| Algorithm | Time | Space | Notes |
|-----------|------|-------|-------|
| Graph parsing | O(E) | O(V + E) | One edge per line |
| Sink elimination | **O(V + E)** | O(V) | Each vertex/edge processed at most once |
| Cycle detection (DFS) | **O(V + E)** | O(V) | Colour map + parent map + explicit stack |

---

## Project Structure

```
Directed-Graph-Acyclicity-Checker/
│
├── src/
│   ├── Main.java             # Entry point — orchestrates parsing, algorithms, output
│   ├── DirectedGraph.java    # Adjacency-list graph with dual HashMap (out + in edges)
│   ├── GraphParser.java      # Reads edge-list text files into DirectedGraph
│   ├── SinkElimination.java  # Algorithm 1 — O(V+E) acyclicity check
│   └── CycleDetector.java    # Algorithm 2 — O(V+E) iterative DFS cycle finder
│
├── benchmarks/
│   ├── acyclic/              # a_40_0.txt … a_10240_4.txt
│   └── cyclic/               # c_40_0.txt … c_10240_4.txt
│
├── .gitignore
└── README.md
```

---

## How to Run

**Requirements:** JDK 11 or later.

```bash
# 1. Clone the repository
git clone https://github.com/<your-username>/directed-graph-acyclicity-checker.git
cd directed-graph-acyclicity-checker

# 2. Compile all source files
javac src/*.java -d out/

# 3. Run with an acyclic benchmark
java -cp out Main benchmarks/acyclic/a_40_0.txt

# 4. Run with a cyclic benchmark
java -cp out Main benchmarks/cyclic/c_40_0.txt
```

---

## Input File Format

A plain-text edge list — one directed edge per line, two integers separated by whitespace:

```
# Optional comments (ignored)
# Optional single-integer vertex-count header (also ignored)
40
1 2
3 1
2 5
4 3
```

- Blank lines and lines starting with `#` are skipped.
- Single-integer lines (vertex count headers) are skipped.
- Any line with more or fewer than two integers raises an error.

---

## Sample Output

**Acyclic graph (`a_40_0.txt`)**

```
[Parser] Loaded 40 vertices and 55 edges from "benchmarks/acyclic/a_40_0.txt".

Graph (40 vertices):
  0 -> [12, 34]
  1 -> [5, 22]
  ...

=== Sink Elimination Algorithm ===
Initial graph: 40 vertices

  Step 1: Remove sink 24
           -> vertex 12 is now a new sink
  Step 2: Remove sink 12
  ...

Result: ACYCLIC (yes) – all vertices eliminated as sinks.

==========================================
 Execution Time: 26.655 ms
------------------------------------------
 ANSWER: YES – the graph is ACYCLIC.
 Elimination order: [24, 12, 37, ...]
==========================================
```

**Cyclic graph (`c_40_0.txt`)**

```
[Parser] Loaded 40 vertices and 76 edges from "benchmarks/cyclic/c_40_0.txt".

...sink elimination removes non-cyclic vertices...

Result: CYCLIC (no) – 30 vertices remain with no sink.

==========================================
 Execution Time: 23.899 ms
------------------------------------------
 ANSWER: NO  – the graph is CYCLIC.
 Sinks removed before halting: [27, 50, ...]
 Remaining (cyclic) vertices:  {0, 1, 2, ...}
==========================================

=== Cycle Found ===
Cycle path: 15 -> 20 -> 4 -> 12 -> 32 -> 27 -> 18 -> 9 -> 15
```

---

## Benchmark Results

Empirical runtimes confirming **O(V + E)** linear growth. Doubling the input size yields ratios well below 2.0 — far from the 4.0 ratio that would indicate O(V²).

| Vertices | Acyclic (ms) | Ratio | Cyclic (ms) | Ratio |
|----------|-------------|-------|-------------|-------|
| 40       | 26.655      | —     | 23.899      | —     |
| 80       | 43.949      | 1.65  | 38.241      | 1.60  |
| 160      | 52.071      | 1.18  | 47.795      | 1.25  |
| 320      | 74.520      | 1.43  | 68.320      | 1.42  |

Cyclic runtimes are consistently slightly lower because sink elimination halts early upon detecting a cyclic core, before handing off to the DFS cycle detector.
