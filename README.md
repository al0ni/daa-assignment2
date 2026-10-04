# Assignment 2

Alua Rakhimzhanova, SE-2509

DynamicArray, MyLinkedList and MinHeap for int values.

## Requirements

Java 17 or newer and Maven.

## Run tests

```text
mvn clean test
```

## Run benchmark

```text
mvn -q compile exec:java
```

The results are saved in `results/results.csv`. Each case has 20 warm-up runs and 5 measured runs. The middle time is saved.

## Make graphs

Python is only needed for graphs.

```text
python -m pip install matplotlib
python plot_results.py
```

The graphs are in `results/plots/`.

The report is available as `REPORT.docx` and `REPORT.pdf`.

## GitHub

https://github.com/al0ni/daa-assignment2

Branch: `main`  
Tag: `v1.0`
