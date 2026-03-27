# Integer Matrix Multiplication

## With inline trampoline

### n=512
| Benchmark                                              | Mode | Cnt |   Score |  Error | Units |
| ------------------------------------------------------ | ---: | --: | ------: | -----: | ----- |
| MatrixMultiplicationBenchmark.benchmarkCoroutines      | avgt |   5 |  78.375 | 22.965 | ms/op |
| MatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt |   5 |  59.587 |  2.541 | ms/op |
| MatrixMultiplicationBenchmark.benchmarkManualForkJoin  | avgt |   5 |  54.957 |  1.616 | ms/op |
| MatrixMultiplicationBenchmark.benchmarkSequential      | avgt |   5 | 170.661 |  3.977 | ms/op |

### n=1024
| Benchmark                                              | Mode | Cnt |    Score |   Error | Units |
| ------------------------------------------------------ | ---: | --: | -------: | ------: | ----- |
| MatrixMultiplicationBenchmark.benchmarkCoroutines      | avgt |   5 |  821.104 |  63.324 | ms/op |
| MatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt |   5 |  776.816 | 206.146 | ms/op |
| MatrixMultiplicationBenchmark.benchmarkManualForkJoin  | avgt |   5 |  719.140 |  61.343 | ms/op |
| MatrixMultiplicationBenchmark.benchmarkSequential      | avgt |   5 | 2442.704 | 134.403 | ms/op |


## Without inline trampoline

### n=512
| Benchmark                                              | Mode | Cnt |   Score |  Error | Units |
| ------------------------------------------------------ | ---: | --: | ------: | -----: | ----- |
| MatrixMultiplicationBenchmark.benchmarkCoroutines      | avgt |   5 |  77.382 | 23.440 | ms/op |
| MatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt |   5 |  66.106 | 15.139 | ms/op |
| MatrixMultiplicationBenchmark.benchmarkManualForkJoin  | avgt |   5 |  58.783 | 11.903 | ms/op |
| MatrixMultiplicationBenchmark.benchmarkSequential      | avgt |   5 | 179.414 |  8.764 | ms/op |

### n=1024
| Benchmark                                              | Mode | Cnt |    Score |   Error | Units |
| ------------------------------------------------------ | ---: | --: | -------: | ------: | ----- |
| MatrixMultiplicationBenchmark.benchmarkCoroutines      | avgt |   5 |  861.473 | 192.450 | ms/op |
| MatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt |   5 |  843.495 |  38.636 | ms/op |
| MatrixMultiplicationBenchmark.benchmarkManualForkJoin  | avgt |   5 |  717.650 |  56.298 | ms/op |
| MatrixMultiplicationBenchmark.benchmarkSequential      | avgt |   5 | 2466.912 | 128.607 | ms/op |

# Double Matrix Multiplication

## With inline trampoline

### n=512
| Benchmark                                                    | Mode | Cnt |   Score |  Error | Units |
| ------------------------------------------------------------ | ---: | --: | ------: | -----: | ----- |
| DoubleMatrixMultiplicationBenchmark.benchmarkCoroutines      | avgt |   5 |  99.674 | 57.784 | ms/op |
| DoubleMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt |   5 |  78.309 |  7.862 | ms/op |
| DoubleMatrixMultiplicationBenchmark.benchmarkManualForkJoin  | avgt |   5 |  78.677 | 22.241 | ms/op |
| DoubleMatrixMultiplicationBenchmark.benchmarkSequential      | avgt |   5 | 286.532 | 24.432 | ms/op |


### n=1024
| Benchmark                                                    | Mode | Cnt |    Score |   Error | Units |
| ------------------------------------------------------------ | ---: | --: | -------: | ------: | ----- |
| DoubleMatrixMultiplicationBenchmark.benchmarkCoroutines      | avgt |   5 |  803.325 |  57.472 | ms/op |
| DoubleMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt |   5 |  967.716 | 155.688 | ms/op |
| DoubleMatrixMultiplicationBenchmark.benchmarkManualForkJoin  | avgt |   5 | 1082.468 | 794.607 | ms/op |
| DoubleMatrixMultiplicationBenchmark.benchmarkSequential      | avgt |   5 | 5879.757 | 345.261 | ms/op |
