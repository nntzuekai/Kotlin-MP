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


# Float Matrix Multiplication

### Size 20

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 0.026 | 0.001 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 0.016 | 0.011 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 0.013 | 0.006 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 0.007 | 0.001 | ms/op |

### Size 30

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 0.041 | 0.002 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 0.029 | 0.014 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 0.022 | 0.001 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 0.022 | 0.001 | ms/op |

### Size 50

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 0.081 | 0.004 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 0.064 | 0.005 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 0.057 | 0.001 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 0.102 | 0.002 | ms/op |

### Size 100

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 0.392 | 0.018 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 0.321 | 0.016 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 0.315 | 0.008 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 0.872 | 0.007 | ms/op |

### Size 150

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 1.265 | 0.056 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 1.072 | 0.120 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 1.013 | 0.019 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 3.064 | 0.150 | ms/op |

### Size 200

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 2.988 | 0.128 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 2.428 | 0.386 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 2.367 | 0.096 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 7.146 | 0.067 | ms/op |

### Size 500

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 51.617 | 1.631 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 39.950 | 4.059 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 41.600 | 7.681 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 116.105 | 2.684 | ms/op |

### Size 1000

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 460.282 | 13.431 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 400.632 | 27.224 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 385.381 | 16.092 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 1004.085 | 70.662 | ms/op |