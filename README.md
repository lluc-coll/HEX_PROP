# Hex Game Documentation

This project is part of an assignment for UPC, where the goal was to implement a Hex game AI using various optimization techniques. My colleague and I were given the graphical part of the game, and we had to implement an heuristic solution to beat our classmates.

## Table of Contents

1. [Heuristic Used and Testing Method](#heuristic-used-and-testing-method)
   - Heuristic Evaluation Function
   - Dijkstra Algorithm
   - Move Optimization
   - Strategic Evaluation
   - Flexibility of Heuristic
   - Testing Method
2. [Graph-Based Optimization Strategy](#graph-based-optimization-strategy)
   - Graph Structure and Algorithm
   - Optimization of updating graphs
   - Hash Tables
   - Pruning

---

## Heuristic Used and Testing Method

### Heuristic Evaluation Function

The heuristic class is designed to evaluate the game state and optimize moves using strategies based on graphs and hashing. This is vital for guiding the Minimax and Iterative Minimax algorithms.

#### Heuristic Formula:
- **Heuristic 1**: `(1000 - current player distance) - (1000 - opponent distance) * 4`
- **Heuristic 2**: `(900 - current player "tuples") - (900 - opponent "tuples") * 2`

The priority is to position pieces in the center, improving connectivity and strategic control.

#### Interpretation:
- A positive value favors the current player.
- A negative value favors the opponent.

### Dijkstra Algorithm

Dijkstra's algorithm is used to find the shortest path between nodes, which optimizes the evaluation of game states. The graph-based approach improves computational efficiency, especially when calculating optimal moves.

### Move Optimization

#### Hash Table:
Each game state is assigned a unique identifier using a hash function. This allows us to detect previously explored states and avoid redundant calculations.

#### Best Move Map:
This stores the best move for each hash, prioritizing promising moves during the search.

#### Pruning:
This technique involves discarding certain states that do not contribute to an optimal solution, thereby improving the algorithm’s efficiency.

#### Static Value (`valEstatic`):
The static value measures the positional advantage on the board. It is updated each time a new piece is placed and is used to prioritize potential moves before executing the Minimax algorithm.

### Strategic Evaluation
Using graphs enables the heuristic to consider global connectivity rather than just local cell values. This approach prioritizes moves that maximize opportunities for the current player while blocking the opponent’s connections.

### Flexibility
The heuristic class can easily be adapted to adjust edge weights or add new evaluation metrics according to the game's needs.

---

## Testing Method

### Manual Testing Approach

The testing process involved executing multiple simulated Hex games with automated players using the `PlayerMinimax` and `PlayerID` classes. The goal was to ensure that the game functions correctly and the heuristic optimization works as expected.

#### Error Identification:
Errors were detected through controlled runs, where specific cases deviated from expected behavior. Common issues included:
- Failures in graph structure management.
- Inconsistencies in heuristic calculations.
- Problems with alpha-beta pruning.

#### Iterative Improvements:
Each identified issue was analyzed, fixed, and validated:
- Adjusting the weight distribution in the graph.
- Optimizing move exploration to prioritize strategic moves.
- Refining pruning and best move table management to avoid redundant calculations.

This iterative process allowed for continuous improvement of the efficiency and effectiveness of the algorithm.

---

## Graph-Based Optimization Strategy

### Graphs

The Hex board is represented as a graph with nodes and edges:
- **Nodes** represent the cells of the board.
- **Edges** connect adjacent nodes and are weighted based on the state of the node:
  - Low weight for empty nodes.
  - High weight for nodes occupied by the opponent.
  - Zero weight for nodes occupied by the current player.
  - Weight 1 for edges connecting two consecutive empty cells, forming a strategic "tuple."

#### Initial and Final Nodes:
- **`ini`**: Represents the player's starting side (first row or column).
- **`end`**: Represents the opponent’s target side (last row or column).

### Dijkstra’s Algorithm

Dijkstra’s algorithm is applied to calculate the shortest paths between the `ini` and `end` nodes, representing the current player and the opponent, respectively. There are two graphs:
- **`graf1`**: Represents the current player's connections.
- **`graf2`**: Represents the opponent's connections.

#### Graph Update:
Each time a piece is placed:
- **`graf1` and `graf2`** are updated to include or modify connections between adjacent nodes.
- The `ini` and `end` nodes are checked and adjusted if the move impacts these borders.
- The edge weights are recalculated depending on the new state of the board.

---

### Optimization of updating graphs

To avoid the computational overhead of iterating through the entire matrix every time a piece is placed, we optimized the graph update process by tracking and updating only the affected nodes. Specifically, we maintained the state of each graph (graf1 for the current player and graf2 for the opponent) and updated only the nodes that could potentially change due to the new move.

This optimization strategy was crucial for performance, as it allowed us to focus on local changes rather than recalculating the entire board state. However, the complexity of this approach grew significantly due to the presence of "tuples" — pairs of adjacent empty cells that are strategically important. Each time a piece was placed, we had to examine not only the directly affected cells but also any nearby "tuples" that might be impacted. This added a layer of complexity, as each "tuple" could involve multiple variables and neighboring connections, requiring careful tracking of state transitions.

Despite these challenges, this approach greatly reduced the number of unnecessary recalculations and allowed the algorithm to be able to reach further down in the minimax algorythm.

---

### Hash Tables

To efficiently identify unique board states and avoid recomputing evaluations, the system uses hash tables.

#### Hash Calculation:
Each position on the board `(x, y)` is mapped to a value. The game state hash is computed by applying the XOR operator on the corresponding values for all cells.

#### Hash Update:
When a piece is placed, the hash is updated in constant time by XORing the old hash with the new position's value.

#### Benefits:
- **Avoids Redundant Calculations**: If a state has been encountered before, its evaluation is reused.
- **Stores Best Moves**: The best move for each hash is saved in the `betterMove` table.
- **Pruning**: States marked as non-optimal are saved in the `pruned` table and excluded from future explorations.

### Pruning

Pruning reduces the number of explored states by marking undesirable states for future exclusion.

#### Impact:
Pruning dramatically reduces the number of nodes explored in the decision tree, which leads to faster computations, especially in complex game scenarios.

---

## Conclusion

This document provides a comprehensive overview of the Hex game implementation, from the heuristics and algorithms used to optimize the gameplay to the testing and optimization strategies employed. The iterative development process ensures a highly efficient and effective Hex AI capable of competing in complex scenarios.

Done by [lluc-coll](https://github.com/lluc-coll) and [danipenalba](https://github.com/danipenalba)
