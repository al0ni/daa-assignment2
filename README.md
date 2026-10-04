# Assignment 2 - Data Structures

Alua Rakhimzhanova, SE-2509

DynamicArray, MyLinkedList and MinHeap for int values. Both bonus tasks are implemented.

## Requirements

Java 17 or newer and Maven.

## Run tests

```text
mvn clean test dependency:copy-dependencies
```

## Run benchmark

```text
java -cp target/classes kz.edu.daa.Benchmark
java "-Djdk.attach.allowAttachSelf=true" "-XX:+EnableDynamicAgentLoading" -cp "target/classes;target/dependency/*" kz.edu.daa.MemoryBenchmark
```

The memory command is for Windows and Java 21+. On Java 17, omit `-XX:+EnableDynamicAgentLoading`. On macOS/Linux, replace the classpath semicolon with a colon. Check JOL output for attachment warnings.

The results are saved in `results/results.csv`, `results/build_heap.csv` and `results/memory.csv`. Each timed case has 20 discarded warmups and 5 measured runs. The median is saved. Inputs, list setup and validation are outside timing. W4 includes insertion and extraction; BUILD includes only construction, allocation and copying. All measured counter totals must match. Inputs use `new Random(42)`.

## Make graphs

Python is only needed for graphs.

```text
python -m pip install matplotlib reportlab
python plot_results.py
python make_report.py
```

The graphs are in `results/plots/`.

The report is available as `REPORT.md` and `REPORT.pdf`.

## Workloads

W1 performs 10,000 reads. W2 performs 1,000 searches with exactly 500 present targets. W3 inserts 1,000 values at a fixed index, then removes 1,000 there. The middle index is the original n/2. W4 inserts n values and extracts all n values. Sizes are 100, 1,000, 10,000 and 100,000. BUILD compares random and descending inputs. Memory includes each structure's complete reachable graph, including Metrics and unused capacity.

## Counters

- `steps`: explicit array-cell reads/writes inside a structure method, or reads of node `next` links, including null. Reading head, tail or node values is not a traversal.
- `moves`: copies/shifts of existing array values or stored head/tail/next updates. A swap is two moves. A new-value write is a step but not a shift.
- `comparisons`: key comparisons, excluding index and loop checks.

Local variables, counter updates, implicit allocation initialization and diagnostic snapshots are excluded. These are source-level events, not CPU instructions. Floyd counts input-array reads inside buildHeap; caller input reads for repeated insertion are outside the structure. Timing includes both approaches' input reads.

## Code map

DynamicArray doubles capacity when full. Insertion shifts values right; removal shifts them left. MyLinkedList stores int values in nodes and keeps a tail for constant-time append. Finding an index follows next links from head. MinHeap stores a complete tree in an array: parent `(i-1)/2`, children `2*i+1` and `2*i+2`. Insert repairs upward. Extraction replaces the root with the last value and repairs downward. buildHeap repairs internal nodes from right to left. Metrics.reset starts a new measurement. Standard collections are used only as test oracles.

## GitHub

https://github.com/al0ni/daa-assignment2

Branch: `main`  
Tag: `v1.0`

Existing commits remain in history. Corrections use the array, list, heap and metrics feature branches. The original release is preserved as `v1.0-original`; `v1.0` identifies the corrected submission. Commit dates are actual creation times. Measurements: Java 25.0.1, Windows 11, AMD Ryzen 5 5500U.
