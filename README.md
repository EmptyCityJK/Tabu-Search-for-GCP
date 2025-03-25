# Tabu Search for GCP
## Overview
This repository contains a Java implementation of the Tabu Search algorithm for solving the Graph Coloring Problem (GCP). The algorithm uses a meta-heuristic approach to find a valid coloring of a graph with a given number of colors while minimizing conflicts.
## Features
- Efficient implementation of Tabu Search for graph coloring
  > tabu[v][c] = tabu tenure
- Dynamic tabu tenure management
  > tabu tenure = iter + conf + random.nextInt(10);
- Conflict reduction through neighbor color analysis
  > scores = adjColor[v][c_old] - adjColor[v][c_new];
- Support for standard DIMACS graph format input
## Requirements
```powershell
java 23.0.1
```
## Input Format
The algorithm accepts graphs in standard DIMACS format. Example:
```Java
c a comment about the data
p edge 5 6
e 1 2
e 1 3
e 2 4
e 3 4
e 3 5
e 4 5
```
## How to run
```powershell
DSJC500.5.col ## the data file you use
48 ## the given color number
5 ## tabu search's running round
```
## Performance
The algorithm is designed to handle medium-sized graphs efficiently, especially `DSJC500.5`, whose best known is 48.
|Instance|Best Known|Our Result|Gap|
| :---: | :---: | :---: | :---: |
|DSJC500.5|48|50|+2|
## License
This project is licensed under the MIT License - see the LICENSE file for details.