# Dijkstra

## Generate Test Data
```
1. Compile: javac GenerateData.java
2. Run: javac GenerateData // wait some time to the program to be end
```

## Run Dijkstra
```
1. Compile: make all
2. Run: java ParallelizedDijkstra // wait some time
3. Run: java ParallelizedVersion // wait some time
```


## Result
| Data Size(Vertice Number / Edges Number)       | Parallelized (ms) (with 4 threads)        | Serial  |
| ------------- |:-------------:| -----:|
| 5/20      | 2 | 0 |
| 10/90      | 7      |   0 |
| 100/9900 | 75     |    0 |
| 1000/999000 | 622      |    35 |
| 2000/3998000 | 2104      |    64 |
| 3000/8997000 | 2424      |    129 |
| 4000/15996000 | 11505      |    299 |
## About
1. ParallelizedVersion.java is the parallelized version of Dijkstra.
2. SerializedVersion.java is the serialized version of Dijkstra.
3. Utils.java includes many utility methods, such as the DataGenerator.


## TestData Format

`the format for TestData1, TestData2, TestData3`

N, start, end, targetDistance

vertex1, vertex2, disBetweenVertice

vertex1, vertex2, disBetweenVertice

...

vertex1, vertex2, disBetweenVertice

`the format for LargeTestData*`

`Every LargeTestData has N * (N - 1) edges`

N, start, end

vertex1, vertex2, disBetweenVertice

vertex1, vertex2, disBetweenVertice

...

vertex1, vertex2, disBetweenVertice