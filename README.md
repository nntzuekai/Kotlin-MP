# Kotlin MP Project

`kotlin-mp-project` is a Kotlin/JVM experiment that brings an OpenMP-style programming model to Kotlin through a small DSL plus a custom compiler plugin.

The project lets you write code like:

```kotlin
omp {
    parallelFor(0 until n, Schedule.Dynamic(4)) { i ->
        work(i)
    }
}
```

and lowers that DSL at compile time into runtime support calls backed by `ForkJoinPool.commonPool()`.

## What It Includes

This repository is split into three Gradle modules:

- `kotlin-mp-runtime`: the public DSL and runtime support functions such as `omp`, `parallelFor`, `parallel`, `barrier`, and `critical`.
- `kotlin-mp-compiler`: a custom Kotlin IR compiler plugin that rewrites DSL calls into optimized runtime trampolines.
- `kotlin-mp-tests`: JUnit tests and JMH benchmarks that exercise correctness and compare performance against sequential code, coroutines, and manual `ForkJoinPool` implementations.

## Feature Summary

- OpenMP-style `omp { ... }` entry point
- `parallelFor` over both `IntRange` and `IntProgression`
- Static scheduling
- Static chunked scheduling
- Dynamic scheduling
- Dynamic chunked scheduling
- `parallel { ... }` regions with optional `numThreads`
- `barrier()` inside parallel regions
- `critical { ... }` and named `critical("name") { ... }`
- Compile-time schedule validation through the compiler plugin

## How It Works

At source level, the DSL functions in `kotlin-mp-runtime` look simple and mostly execute sequential placeholder code. The real behavior comes from the compiler plugin in `kotlin-mp-compiler`.

During compilation, the plugin:

- intercepts `OmpContext.parallelFor(...)`
- determines whether the loop is a range or progression
- checks the schedule shape and chunk-size constraints
- rewrites the call to one of the hidden runtime helpers such as `executeParallelRangeStatic` or `executeParallelProgressionDynamicChunked`

It also intercepts `parallel { ... }` regions:

- if the block contains `barrier()`, it routes to a barrier-enabled runtime path using `Phaser`
- otherwise, it routes to a lighter barrier-free runtime path

## Example Usage

### Basic parallel loop

```kotlin
import com.rkh.kotlinmp.Schedule
import com.rkh.kotlinmp.omp

fun add(a: IntArray, b: IntArray): IntArray {
    val out = IntArray(a.size)

    omp {
        parallelFor(0 until a.size) { i ->
            out[i] = a[i] + b[i]
        }
    }

    return out
}
```

### Explicit scheduling

```kotlin
omp {
    parallelFor(0 until n, Schedule.Static) { i ->
        work(i)
    }
}

omp {
    parallelFor(0 until n, Schedule.Static(8)) { i ->
        work(i)
    }
}

omp {
    parallelFor(0 until n, Schedule.Dynamic()) { i ->
        work(i)
    }
}

omp {
    parallelFor(0 until n, Schedule.Dynamic(4)) { i ->
        work(i)
    }
}
```

### Parallel region with barrier

```kotlin
omp {
    parallel(numThreads = 4) {
        phaseOne()
        barrier()
        phaseTwo()
    }
}
```

### Critical sections

```kotlin
omp {
    parallelFor(0 until 1000, Schedule.Dynamic(10)) {
        critical {
            updateSharedState()
        }

        critical("histogram") {
            updateHistogram()
        }
    }
}
```

## Compile-Time Rules and Constraints

The current compiler plugin is opinionated and intentionally restrictive:

- schedules must be passed inline, not through a general `Schedule` variable
- chunk sizes must be integer literals or `const val` values
- chunk sizes must be `>= 1`
- `parallelFor` defaults to static scheduling when no schedule is provided
- `Schedule.Dynamic(1)` is optimized by the plugin to the default dynamic path
- barrier-free `parallel { ... }` blocks cannot reference the `ParallelScope` receiver

There is also one important compiler limitation in the current implementation:

- the custom plugin declares `supportsK2 = false`, so this project is tied to the classic compiler pipeline rather than K2

## TODO

- Add `critical()` support inside `parallel { ... }` without regressing the zero-allocation barrier-free fast path.

Detailed design:

- At the DSL level, add `critical(block)` and `critical(name, block)` to `ParallelScope` so that `parallel { critical { ... } }` type-checks naturally.
- Do not rewrite `ParallelScope.critical()` calls to `OmpContext.critical()` in the IR plugin. That approach is fragile because `OmpContext.critical()` is an instance member, so the plugin would need to retarget both the call symbol and the dispatch receiver to a valid outer `OmpContext` for every rewritten call site.
- Instead, introduce shared receiver-free runtime helpers, for example `executeCritical(block)` and `executeCritical(name, block)`, that contain the actual lock logic.
- Make both `OmpContext.critical()` and `ParallelScope.critical()` delegate to those shared helpers.
- In the IR plugin, when lowering `parallel { ... }`:
  - If the block uses `barrier()`, route to `executeParallelRegionWithBarrier` as today.
  - If the block does not use `barrier()`, rewrite any `ParallelScope.critical()` calls to the shared receiver-free helpers.
  - After rewriting `critical()` calls, re-check whether the lambda still uses the `ParallelScope` receiver in any other way.
  - If no receiver use remains, strip the lambda receiver and route to `executeParallelRegionWithoutBarrier` with type `() -> Unit`.
  - If receiver use still remains for some other reason, fall back to a scoped no-barrier path such as `executeParallelRegionWithoutBarrierScoped(ParallelScope.() -> Unit)`.

Why this design:

- It allows `parallel { critical { ... } }` in the DSL.
- It preserves the fully receiver-free no-barrier fast path when `critical()` is the only reason the receiver was needed.
- It avoids brittle IR rewrites that depend on reconstructing an outer `OmpContext` receiver.

## Requirements

- JDK 17
- Gradle wrapper included in the repository
- Kotlin `1.9.23`

The project is configured as a JVM-only Kotlin build.

## Build

Build all modules:

```bash
./gradlew build
```

Build a single module:

```bash
./gradlew :kotlin-mp-runtime:build
./gradlew :kotlin-mp-compiler:build
./gradlew :kotlin-mp-tests:build
```

## Run Tests

The correctness tests live in `kotlin-mp-tests/src/test/kotlin`.

Run all tests:

```bash
./gradlew test
```

Run only the tests module:

```bash
./gradlew :kotlin-mp-tests:test
```

The current test suite covers:

- default static scheduling
- static chunked scheduling
- dynamic scheduling
- dynamic chunked scheduling
- range and progression lowering
- parallel regions with and without barriers
- unnamed and named critical sections

## Run Benchmarks

JMH benchmarks live in `kotlin-mp-tests/src/jmh/kotlin`.

Run the configured benchmark set:

```bash
./gradlew :kotlin-mp-tests:jmh
```

The benchmarks compare:

- sequential loops
- Kotlin coroutines
- manual `ForkJoinPool` code
- Kotlin MP DSL lowered by the compiler plugin

Current benchmark classes include:

- `FloatMatrixMultiplicationBenchmark`
- `DoubleMatrixMultiplicationBenchmark`
- `IntMatrixMultiplicationBenchmark`
- `MandelbrotBenchmark`
- `StaticBenchmark`

The Gradle JMH configuration is currently set to include only `FloatMatrixMultiplicationBenchmark` by default in [`kotlin-mp-tests/build.gradle.kts`](/home/rkh/CS6245/kotlin-mp-project/kotlin-mp-tests/build.gradle.kts:1).

## Benchmark Artifacts

The repository already contains benchmark output and plots:

- [`docs/scratch.md`](/home/rkh/CS6245/kotlin-mp-project/docs/scratch.md:1)
- [`benchmark_plot.png`](/home/rkh/CS6245/kotlin-mp-project/benchmark_plot.png)
- [`plots/float_linear.png`](/home/rkh/CS6245/kotlin-mp-project/plots/float_linear.png)
- [`plots/float_log.png`](/home/rkh/CS6245/kotlin-mp-project/plots/float_log.png)

There is also a helper script for turning JMH-style text into markdown tables and plots:

- [`scripts/benchmark_plot.py`](/home/rkh/CS6245/kotlin-mp-project/scripts/benchmark_plot.py:1)

## Project Layout

```text
.
├── build.gradle.kts
├── settings.gradle.kts
├── kotlin-mp-runtime/
│   └── src/main/kotlin/com/rkh/kotlinmp/
├── kotlin-mp-compiler/
│   ├── src/main/kotlin/com/rkh/kotlinmp/compiler/
│   └── src/main/resources/META-INF/services/
├── kotlin-mp-tests/
│   ├── src/test/kotlin/com/rkh/kotlinmp/tests/
│   └── src/jmh/kotlin/com/rkh/kotlinmp/benchmark/
├── docs/
└── scripts/
```

## Notes

- The tests module injects the compiler plugin into Kotlin compilation through a custom Gradle configuration and `-Xplugin=...` compiler arguments.
- The runtime uses `ForkJoinPool.commonPool()` rather than creating a dedicated worker pool.
- The repository currently contains generated build outputs under `build/`, which is useful for inspection but not usually something you would commit in a production project.
