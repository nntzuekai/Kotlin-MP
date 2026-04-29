package com.rkh.kotlinmp.benchmark

import com.rkh.kotlinmp.*
import com.rkh.kotlinmp.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.*
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinTask
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
open class MandelbrotBenchmark {

    @Param("1024", "2048")
    var resolution: Int = 0

    // The "weight" of the compute.
    // 5000 means pixels deep inside the set take 5000 loop iterations to escape.
    private val maxIterations = 5000

    lateinit var pixels: IntArray

    @Setup(Level.Trial)
    fun setup() {
        pixels = IntArray(resolution * resolution)
    }

    // --- The Core Math Closure ---
    // Extracted so the exact same math instructions are used in every approach.
    private fun computeRow(y: Int) {
        // Map Y pixel to the imaginary plane (-1.2 to 1.2)
        val cy = (y.toDouble() / resolution) * 2.4 - 1.2

        for (x in 0 until resolution) {
            // Map X pixel to the real plane (-2.0 to 0.5)
            val cx = (x.toDouble() / resolution) * 2.5 - 2.0

            var zx = 0.0
            var zy = 0.0
            var iteration = 0

            // The Escape-Time Algorithm: Z_{n+1} = Z_n^2 + C
            while (zx * zx + zy * zy <= 4.0 && iteration < maxIterations) {
                val zxTemp = zx * zx - zy * zy + cx
                zy = 2.0 * zx * zy + cy
                zx = zxTemp
                iteration++
            }

            pixels[y * resolution + x] = iteration
        }
    }

    // 1. THE BASELINE
    @Benchmark
    fun benchmarkSequential() {
        for (y in 0 until resolution) {
            computeRow(y)
        }
    }

    // 2. KOTLIN COROUTINES
    // Allocates `resolution` number of Deferred state machines on the heap.
    @Benchmark
    fun benchmarkCoroutines() = runBlocking(Dispatchers.Default) {
        (0 until resolution).map { y ->
            async {
                computeRow(y)
            }
        }.awaitAll()
    }

    // 3. MANUAL FORKJOIN (Dynamic Work-Stealing)
    // Simulating exactly what your IR compiler generates for Schedule.Dynamic
    @Benchmark
    fun benchmarkManualForkJoinDynamic() {
        val pool = ForkJoinPool.commonPool()
        val numThreads = pool.parallelism.coerceAtLeast(1)

        // The shared queue index!
        val rowIndex = AtomicInteger(0)

        val tasks = (0 until numThreads).map {
            Runnable {
                while (true) {
                    val y = rowIndex.getAndIncrement()
                    if (y >= resolution) break
                    computeRow(y)
                }
            }
        }
        val futures = tasks.map { pool.submit(it) }
        futures.forEach { it.get() }
    }

    // 4. YOUR COMPILER PLUGIN (The OpenMP Way)
    @Benchmark
    fun benchmarkKotlinMpDynamic() {
        omp {
            // Your DSL cleanly abstracts the complex AtomicInteger while(true) loop
            parallelFor(0 until resolution, Schedule.Dynamic) { y ->
                computeRow(y)
            }
        }
    }
}