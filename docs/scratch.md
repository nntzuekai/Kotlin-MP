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
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 0.027 | 0.001 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 0.015 | 0.005 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 0.012 | 0.001 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 0.007 | 0.001 | ms/op |

### Size 30

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 0.041 | 0.003 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 0.026 | 0.006 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 0.023 | 0.004 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 0.022 | 0.001 | ms/op |

### Size 31

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 0.043 | 0.004 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 0.028 | 0.002 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 0.025 | 0.001 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 0.024 | 0.001 | ms/op |

### Size 32

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 0.044 | 0.001 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 0.026 | 0.001 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 0.026 | 0.001 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 0.027 | 0.002 | ms/op |

### Size 50

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 0.082 | 0.001 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 0.056 | 0.001 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 0.057 | 0.004 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 0.105 | 0.011 | ms/op |

### Size 96

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 0.453 | 0.132 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 0.275 | 0.006 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 0.285 | 0.040 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 0.794 | 0.116 | ms/op |

### Size 97

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 0.426 | 0.036 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 0.294 | 0.012 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 0.285 | 0.010 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 0.796 | 0.007 | ms/op |

### Size 100

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 0.484 | 0.114 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 0.314 | 0.003 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 0.321 | 0.004 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 0.893 | 0.009 | ms/op |

### Size 127

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 0.915 | 0.067 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 0.668 | 0.262 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 0.625 | 0.007 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 1.807 | 0.017 | ms/op |

### Size 128

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 0.871 | 0.050 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 0.728 | 0.010 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 0.731 | 0.020 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 1.994 | 0.029 | ms/op |

### Size 129

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 0.835 | 0.092 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 0.685 | 0.111 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 0.646 | 0.008 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 1.918 | 0.012 | ms/op |

### Size 150

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 1.256 | 0.044 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 1.008 | 0.009 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 1.005 | 0.011 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 3.058 | 0.033 | ms/op |

### Size 200

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 2.810 | 0.056 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 2.354 | 0.063 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 2.387 | 0.363 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 7.136 | 0.154 | ms/op |

### Size 500

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 48.692 | 0.974 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 39.289 | 1.071 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 40.322 | 5.435 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 116.735 | 0.671 | ms/op |

### Size 600

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 83.522 | 1.450 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 76.471 | 26.560 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 72.617 | 21.280 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 205.029 | 22.083 | ms/op |

### Size 700

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 144.260 | 2.347 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 135.277 | 39.186 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 119.822 | 2.364 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 331.547 | 2.673 | ms/op |

### Size 1000

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| FloatMatrixMultiplicationBenchmark.benchmarkCoroutines | avgt | 5 | 455.101 | 38.608 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkKotlinMpDefault | avgt | 5 | 402.776 | 27.591 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkManualForkJoin | avgt | 5 | 385.827 | 26.275 | ms/op |
| FloatMatrixMultiplicationBenchmark.benchmarkSequential | avgt | 5 | 994.084 | 15.118 | ms/op |

# Irregular Load Benchmark

### Size 1000

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| IrregularLoadBenchmark.coroutines | avgt | 5 | 0.583 | 0.017 | ms/op |
| IrregularLoadBenchmark.kotlinMpDynamic | avgt | 5 | 0.137 | 0.002 | ms/op |
| IrregularLoadBenchmark.kotlinMpDynamicChunked | avgt | 5 | 0.138 | 0.001 | ms/op |
| IrregularLoadBenchmark.kotlinMpStatic | avgt | 5 | 0.582 | 0.005 | ms/op |
| IrregularLoadBenchmark.manualForkJoinDynamic | avgt | 5 | 0.119 | 0.001 | ms/op |
| IrregularLoadBenchmark.manualForkJoinStatic | avgt | 5 | 0.581 | 0.008 | ms/op |
| IrregularLoadBenchmark.sequential | avgt | 5 | 0.564 | 0.006 | ms/op |

### Size 3000

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| IrregularLoadBenchmark.coroutines | avgt | 5 | 1.674 | 0.071 | ms/op |
| IrregularLoadBenchmark.kotlinMpDynamic | avgt | 5 | 0.388 | 0.020 | ms/op |
| IrregularLoadBenchmark.kotlinMpDynamicChunked | avgt | 5 | 0.342 | 0.022 | ms/op |
| IrregularLoadBenchmark.kotlinMpStatic | avgt | 5 | 1.681 | 0.018 | ms/op |
| IrregularLoadBenchmark.manualForkJoinDynamic | avgt | 5 | 0.374 | 0.095 | ms/op |
| IrregularLoadBenchmark.manualForkJoinStatic | avgt | 5 | 1.676 | 0.020 | ms/op |
| IrregularLoadBenchmark.sequential | avgt | 5 | 1.688 | 0.003 | ms/op |

### Size 5000

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| IrregularLoadBenchmark.coroutines | avgt | 5 | 2.769 | 0.163 | ms/op |
| IrregularLoadBenchmark.kotlinMpDynamic | avgt | 5 | 0.634 | 0.028 | ms/op |
| IrregularLoadBenchmark.kotlinMpDynamicChunked | avgt | 5 | 0.566 | 0.098 | ms/op |
| IrregularLoadBenchmark.kotlinMpStatic | avgt | 5 | 2.774 | 0.029 | ms/op |
| IrregularLoadBenchmark.manualForkJoinDynamic | avgt | 5 | 0.640 | 0.008 | ms/op |
| IrregularLoadBenchmark.manualForkJoinStatic | avgt | 5 | 2.784 | 0.077 | ms/op |
| IrregularLoadBenchmark.sequential | avgt | 5 | 2.818 | 0.037 | ms/op |

### Size 10000

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| IrregularLoadBenchmark.coroutines | avgt | 5 | 5.603 | 1.011 | ms/op |
| IrregularLoadBenchmark.kotlinMpDynamic | avgt | 5 | 1.248 | 0.009 | ms/op |
| IrregularLoadBenchmark.kotlinMpDynamicChunked | avgt | 5 | 1.097 | 0.025 | ms/op |
| IrregularLoadBenchmark.kotlinMpStatic | avgt | 5 | 5.523 | 0.026 | ms/op |
| IrregularLoadBenchmark.manualForkJoinDynamic | avgt | 5 | 1.257 | 0.013 | ms/op |
| IrregularLoadBenchmark.manualForkJoinStatic | avgt | 5 | 5.517 | 0.022 | ms/op |
| IrregularLoadBenchmark.sequential | avgt | 5 | 5.637 | 0.051 | ms/op |

### Size 15000

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| IrregularLoadBenchmark.coroutines | avgt | 5 | 8.400 | 1.506 | ms/op |
| IrregularLoadBenchmark.kotlinMpDynamic | avgt | 5 | 1.870 | 0.018 | ms/op |
| IrregularLoadBenchmark.kotlinMpDynamicChunked | avgt | 5 | 1.638 | 0.026 | ms/op |
| IrregularLoadBenchmark.kotlinMpStatic | avgt | 5 | 8.250 | 0.065 | ms/op |
| IrregularLoadBenchmark.manualForkJoinDynamic | avgt | 5 | 1.896 | 0.165 | ms/op |
| IrregularLoadBenchmark.manualForkJoinStatic | avgt | 5 | 8.266 | 0.034 | ms/op |
| IrregularLoadBenchmark.sequential | avgt | 5 | 8.434 | 0.033 | ms/op |

### Size 30000

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| IrregularLoadBenchmark.coroutines | avgt | 5 | 17.147 | 0.324 | ms/op |
| IrregularLoadBenchmark.kotlinMpDynamic | avgt | 5 | 3.740 | 0.085 | ms/op |
| IrregularLoadBenchmark.kotlinMpDynamicChunked | avgt | 5 | 3.287 | 0.125 | ms/op |
| IrregularLoadBenchmark.kotlinMpStatic | avgt | 5 | 16.562 | 0.294 | ms/op |
| IrregularLoadBenchmark.manualForkJoinDynamic | avgt | 5 | 3.725 | 0.086 | ms/op |
| IrregularLoadBenchmark.manualForkJoinStatic | avgt | 5 | 16.501 | 0.027 | ms/op |
| IrregularLoadBenchmark.sequential | avgt | 5 | 16.874 | 0.042 | ms/op |

| Benchmark | 1000 | 3000 | 5000 | 10000 | 15000 | 30000 |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| coroutines | 0.583 ± 0.017 | 1.674 ± 0.071 | 2.769 ± 0.163 | 5.603 ± 1.011 | 8.400 ± 1.506 | 17.147 ± 0.324 |
| kotlinMpDynamic | 0.137 ± 0.002 | 0.388 ± 0.020 | 0.634 ± 0.028 | 1.248 ± 0.009 | 1.870 ± 0.018 | 3.740 ± 0.085 |
| kotlinMpDynamicChunked | 0.138 ± 0.001 | 0.342 ± 0.022 | 0.566 ± 0.098 | 1.097 ± 0.025 | 1.638 ± 0.026 | 3.287 ± 0.125 |
| kotlinMpStatic | 0.582 ± 0.005 | 1.681 ± 0.018 | 2.774 ± 0.029 | 5.523 ± 0.026 | 8.250 ± 0.065 | 16.562 ± 0.294 |
| manualForkJoinDynamic | 0.119 ± 0.001 | 0.374 ± 0.095 | 0.640 ± 0.008 | 1.257 ± 0.013 | 1.896 ± 0.165 | 3.725 ± 0.086 |
| manualForkJoinStatic | 0.581 ± 0.008 | 1.676 ± 0.020 | 2.784 ± 0.077 | 5.517 ± 0.022 | 8.266 ± 0.034 | 16.501 ± 0.027 |
| sequential | 0.564 ± 0.006 | 1.688 ± 0.003 | 2.818 ± 0.037 | 5.637 ± 0.051 | 8.434 ± 0.033 | 16.874 ± 0.042 |

| Benchmark | 1000 | 3000 | 5000 | 10000 | 15000 | 30000 |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| coroutines | 0.583 | 1.674 | 2.769  | 5.603  | 8.400 | 17.147 |
| kotlinMpDynamic | 0.137 | 0.388 | 0.634  | 1.248  | 1.870  | 3.740  |
| kotlinMpDynamicChunked | 0.138  | 0.342  | 0.566  | 1.097  | 1.638 | 3.287  |
| kotlinMpStatic | 0.582  | 1.681  | 2.774  | 5.523  | 8.250  | 16.562  |
| manualForkJoinDynamic | 0.119  | 0.374  | 0.640  | 1.257  | 1.896  | 3.725  |
| manualForkJoinStatic | 0.581  | 1.676 | 2.784  | 5.517  | 8.266  | 16.501 |
| sequential | 0.564  | 1.688 | 2.818  | 5.637  | 8.434  | 16.874  |

# Scheduler Overhead Benchmark

### Size 250

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| SchedulerOverheadBenchmark.coroutines | avgt | 5 | 139.012 | 9.973 | us/op |
| SchedulerOverheadBenchmark.kotlinMpDynamic | avgt | 5 | 7.118 | 0.527 | us/op |
| SchedulerOverheadBenchmark.kotlinMpDynamicChunked | avgt | 5 | 6.007 | 0.744 | us/op |
| SchedulerOverheadBenchmark.kotlinMpStatic | avgt | 5 | 5.355 | 0.770 | us/op |
| SchedulerOverheadBenchmark.kotlinMpStaticChunked | avgt | 5 | 5.771 | 0.625 | us/op |
| SchedulerOverheadBenchmark.manualForkJoinDynamic | avgt | 5 | 7.656 | 1.320 | us/op |
| SchedulerOverheadBenchmark.manualForkJoinStatic | avgt | 5 | 5.511 | 0.925 | us/op |
| SchedulerOverheadBenchmark.sequential | avgt | 5 | 0.031 | 0.001 | us/op |

### Size 750

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| SchedulerOverheadBenchmark.coroutines | avgt | 5 | 414.474 | 38.655 | us/op |
| SchedulerOverheadBenchmark.kotlinMpDynamic | avgt | 5 | 20.542 | 0.321 | us/op |
| SchedulerOverheadBenchmark.kotlinMpDynamicChunked | avgt | 5 | 6.425 | 0.033 | us/op |
| SchedulerOverheadBenchmark.kotlinMpStatic | avgt | 5 | 5.605 | 0.490 | us/op |
| SchedulerOverheadBenchmark.kotlinMpStaticChunked | avgt | 5 | 6.051 | 0.117 | us/op |
| SchedulerOverheadBenchmark.manualForkJoinDynamic | avgt | 5 | 21.152 | 0.583 | us/op |
| SchedulerOverheadBenchmark.manualForkJoinStatic | avgt | 5 | 5.519 | 0.121 | us/op |
| SchedulerOverheadBenchmark.sequential | avgt | 5 | 0.036 | 0.001 | us/op |

### Size 1500

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| SchedulerOverheadBenchmark.coroutines | avgt | 5 | 846.824 | 242.891 | us/op |
| SchedulerOverheadBenchmark.kotlinMpDynamic | avgt | 5 | 36.621 | 3.003 | us/op |
| SchedulerOverheadBenchmark.kotlinMpDynamicChunked | avgt | 5 | 6.843 | 1.277 | us/op |
| SchedulerOverheadBenchmark.kotlinMpStatic | avgt | 5 | 5.595 | 0.152 | us/op |
| SchedulerOverheadBenchmark.kotlinMpStaticChunked | avgt | 5 | 8.192 | 0.443 | us/op |
| SchedulerOverheadBenchmark.manualForkJoinDynamic | avgt | 5 | 37.931 | 1.355 | us/op |
| SchedulerOverheadBenchmark.manualForkJoinStatic | avgt | 5 | 5.749 | 0.578 | us/op |
| SchedulerOverheadBenchmark.sequential | avgt | 5 | 0.052 | 0.001 | us/op |

### Size 4000

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| SchedulerOverheadBenchmark.coroutines | avgt | 5 | 2134.161 | 52.748 | us/op |
| SchedulerOverheadBenchmark.kotlinMpDynamic | avgt | 5 | 87.420 | 1.252 | us/op |
| SchedulerOverheadBenchmark.kotlinMpDynamicChunked | avgt | 5 | 11.951 | 0.209 | us/op |
| SchedulerOverheadBenchmark.kotlinMpStatic | avgt | 5 | 6.622 | 0.340 | us/op |
| SchedulerOverheadBenchmark.kotlinMpStaticChunked | avgt | 5 | 13.723 | 0.707 | us/op |
| SchedulerOverheadBenchmark.manualForkJoinDynamic | avgt | 5 | 92.995 | 7.289 | us/op |
| SchedulerOverheadBenchmark.manualForkJoinStatic | avgt | 5 | 5.927 | 0.150 | us/op |
| SchedulerOverheadBenchmark.sequential | avgt | 5 | 0.142 | 0.001 | us/op |

### Size 12000

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| SchedulerOverheadBenchmark.coroutines | avgt | 5 | 6292.292 | 366.979 | us/op |
| SchedulerOverheadBenchmark.kotlinMpDynamic | avgt | 5 | 254.186 | 9.380 | us/op |
| SchedulerOverheadBenchmark.kotlinMpDynamicChunked | avgt | 5 | 27.473 | 1.841 | us/op |
| SchedulerOverheadBenchmark.kotlinMpStatic | avgt | 5 | 11.600 | 1.482 | us/op |
| SchedulerOverheadBenchmark.kotlinMpStaticChunked | avgt | 5 | 24.421 | 1.403 | us/op |
| SchedulerOverheadBenchmark.manualForkJoinDynamic | avgt | 5 | 266.499 | 19.483 | us/op |
| SchedulerOverheadBenchmark.manualForkJoinStatic | avgt | 5 | 6.725 | 1.118 | us/op |
| SchedulerOverheadBenchmark.sequential | avgt | 5 | 0.947 | 0.024 | us/op |

### Size 65000

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| SchedulerOverheadBenchmark.coroutines | avgt | 5 | 36487.596 | 943.443 | us/op |
| SchedulerOverheadBenchmark.kotlinMpDynamic | avgt | 5 | 1350.000 | 15.293 | us/op |
| SchedulerOverheadBenchmark.kotlinMpDynamicChunked | avgt | 5 | 105.755 | 2.177 | us/op |
| SchedulerOverheadBenchmark.kotlinMpStatic | avgt | 5 | 24.399 | 0.528 | us/op |
| SchedulerOverheadBenchmark.kotlinMpStaticChunked | avgt | 5 | 68.641 | 9.066 | us/op |
| SchedulerOverheadBenchmark.manualForkJoinDynamic | avgt | 5 | 1404.413 | 28.849 | us/op |
| SchedulerOverheadBenchmark.manualForkJoinStatic | avgt | 5 | 14.657 | 0.447 | us/op |
| SchedulerOverheadBenchmark.sequential | avgt | 5 | 4.938 | 0.048 | us/op |

### Size 250000

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| SchedulerOverheadBenchmark.coroutines | avgt | 5 | 150788.887 | 7602.025 | us/op |
| SchedulerOverheadBenchmark.kotlinMpDynamic | avgt | 5 | 4900.895 | 146.571 | us/op |
| SchedulerOverheadBenchmark.kotlinMpDynamicChunked | avgt | 5 | 363.990 | 3.841 | us/op |
| SchedulerOverheadBenchmark.kotlinMpStatic | avgt | 5 | 56.647 | 0.660 | us/op |
| SchedulerOverheadBenchmark.kotlinMpStaticChunked | avgt | 5 | 221.184 | 18.357 | us/op |
| SchedulerOverheadBenchmark.manualForkJoinDynamic | avgt | 5 | 5045.221 | 231.628 | us/op |
| SchedulerOverheadBenchmark.manualForkJoinStatic | avgt | 5 | 42.158 | 33.231 | us/op |
| SchedulerOverheadBenchmark.sequential | avgt | 5 | 42.245 | 1.402 | us/op |

### Size 1000000

| Benchmark | Mode | Cnt | Score | Error | Units |
|---|---:|---:|---:|---:|---|
| SchedulerOverheadBenchmark.coroutines | avgt | 5 | 737092.626 | 174977.126 | us/op |
| SchedulerOverheadBenchmark.kotlinMpDynamic | avgt | 5 | 20092.225 | 1413.718 | us/op |
| SchedulerOverheadBenchmark.kotlinMpDynamicChunked | avgt | 5 | 1476.586 | 18.529 | us/op |
| SchedulerOverheadBenchmark.kotlinMpStatic | avgt | 5 | 187.711 | 3.215 | us/op |
| SchedulerOverheadBenchmark.kotlinMpStaticChunked | avgt | 5 | 1027.446 | 794.499 | us/op |
| SchedulerOverheadBenchmark.manualForkJoinDynamic | avgt | 5 | 21589.282 | 2250.619 | us/op |
| SchedulerOverheadBenchmark.manualForkJoinStatic | avgt | 5 | 96.511 | 2.494 | us/op |
| SchedulerOverheadBenchmark.sequential | avgt | 5 | 178.276 | 1.355 | us/op |


| Benchmark | 250 | 750 | 1500 | 4000 | 12000 | 65000 | 250000 | 1000000 |
| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| coroutines | 139.012 ± 9.973 | 414.474 ± 38.655 | 846.824 ± 242.891 | 2134.161 ± 52.748 | 6292.292 ± 366.979 | 36487.596 ± 943.443 | 150788.887 ± 7602.025 | 737092.626 ± 174977.126 |
| kotlinMpDynamic | 7.118 ± 0.527 | 20.542 ± 0.321 | 36.621 ± 3.003 | 87.420 ± 1.252 | 254.186 ± 9.380 | 1350.000 ± 15.293 | 4900.895 ± 146.571 | 20092.225 ± 1413.718 |
| kotlinMpDynamicChunked | 6.007 ± 0.744 | 6.425 ± 0.033 | 6.843 ± 1.277 | 11.951 ± 0.209 | 27.473 ± 1.841 | 105.755 ± 2.177 | 363.990 ± 3.841 | 1476.586 ± 18.529 |
| kotlinMpStatic | 5.355 ± 0.770 | 5.605 ± 0.490 | 5.595 ± 0.152 | 6.622 ± 0.340 | 11.600 ± 1.482 | 24.399 ± 0.528 | 56.647 ± 0.660 | 187.711 ± 3.215 |
| kotlinMpStaticChunked | 5.771 ± 0.625 | 6.051 ± 0.117 | 8.192 ± 0.443 | 13.723 ± 0.707 | 24.421 ± 1.403 | 68.641 ± 9.066 | 221.184 ± 18.357 | 1027.446 ± 794.499 |
| manualForkJoinDynamic | 7.656 ± 1.320 | 21.152 ± 0.583 | 37.931 ± 1.355 | 92.995 ± 7.289 | 266.499 ± 19.483 | 1404.413 ± 28.849 | 5045.221 ± 231.628 | 21589.282 ± 2250.619 |
| manualForkJoinStatic | 5.511 ± 0.925 | 5.519 ± 0.121 | 5.749 ± 0.578 | 5.927 ± 0.150 | 6.725 ± 1.118 | 14.657 ± 0.447 | 42.158 ± 33.231 | 96.511 ± 2.494 |
| sequential | 0.031 ± 0.001 | 0.036 ± 0.001 | 0.052 ± 0.001 | 0.142 ± 0.001 | 0.947 ± 0.024 | 4.938 ± 0.048 | 42.245 ± 1.402 | 178.276 ± 1.355 |
