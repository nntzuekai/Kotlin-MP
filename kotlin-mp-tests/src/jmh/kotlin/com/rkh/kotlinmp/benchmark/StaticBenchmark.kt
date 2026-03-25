package com.rkh.kotlinmp.benchmark

import com.rkh.kotlinmp.Schedule
import com.rkh.kotlinmp.omp
import kotlinx.coroutines.*
import org.openjdk.jmh.annotations.*
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class StaticBenchmark {

    private val size = 5_000_000
    private lateinit var data: DoubleArray
    private lateinit var results: DoubleArray

    @Setup(Level.Trial)
    fun setup() {
        data = DoubleArray(size) { it.toDouble() }
        results = DoubleArray(size)
    }

    // 1. BASELINE: STANDARD KOTLIN FOR-LOOP
    @Benchmark
    fun sequentialMath() {
        for (i in 0 until size) {
            val v = data[i]
            results[i] = sqrt(sin(v) * sin(v) + cos(v) * cos(v))
        }
    }

    // 2. YOUR COMPILER: PARALLEL AUTO-BLOCK (Fast Path)
    @Benchmark
    fun parallelCompilerAutoBlock() {
        omp {
            parallelFor(0 until size, Schedule.Static) { i ->
                val v = data[i]
                results[i] = sqrt(sin(v) * sin(v) + cos(v) * cos(v))
            }
        }
    }

    // 3. YOUR COMPILER: PARALLEL CYCLIC (Round-Robin)
    @Benchmark
    fun parallelCompilerCyclic() {
        omp {
            parallelFor(0 until size, Schedule.Static(10)) { i ->
                val v = data[i]
                results[i] = sqrt(sin(v) * sin(v) + cos(v) * cos(v))
            }
        }
    }

    // 4. COMPARISON: HAND-WRITTEN FORK-JOIN-POOL
    @Benchmark
    fun parallelManualForkJoin() {
        val pool = ForkJoinPool.commonPool()
        val numThreads = pool.parallelism.coerceAtLeast(1)
        val chunkSize = (size + numThreads - 1) / numThreads

        val tasks = (0 until numThreads).mapNotNull { threadId ->
            val start = threadId * chunkSize
            if (start >= size) null
            else {
                val endInclusive = minOf(start + chunkSize - 1, size - 1)
                Runnable {
                    for (i in start..endInclusive) {
                        val v = data[i]
                        results[i] = sqrt(sin(v) * sin(v) + cos(v) * cos(v))
                    }
                }
            }
        }
        val futures = tasks.map { pool.submit(it) }
        futures.forEach { it.get() }
    }

    // 5. COMPARISON: KOTLIN COROUTINES
    @Benchmark
    fun parallelCoroutines() = runBlocking {
        val numThreads = Runtime.getRuntime().availableProcessors()
        val chunkSize = (size + numThreads - 1) / numThreads

        // We use Dispatchers.Default as it is backed by a CPU-core-sized thread pool
        val deferreds = (0 until numThreads).map { threadId ->
            async(Dispatchers.Default) {
                val start = threadId * chunkSize
                val endInclusive = minOf(start + chunkSize - 1, size - 1)
                if (start < size) {
                    for (i in start..endInclusive) {
                        val v = data[i]
                        results[i] = sqrt(sin(v) * sin(v) + cos(v) * cos(v))
                    }
                }
            }
        }
        deferreds.awaitAll()
    }
}