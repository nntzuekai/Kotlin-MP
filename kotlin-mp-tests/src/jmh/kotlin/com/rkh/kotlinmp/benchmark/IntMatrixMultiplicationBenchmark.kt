package com.rkh.kotlinmp.benchmark

import com.rkh.kotlinmp.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.*
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
open class IntMatrixMultiplicationBenchmark {

    @Param("512", "1024")
    var size: Int = 0

    lateinit var matrixA: IntArray
    lateinit var matrixB: IntArray
    lateinit var matrixC: IntArray

    @Setup(Level.Trial)
    fun setup() {
        val totalElements = size * size
        // Using modulo to prevent integer overflow during the O(N^3) multiplication
        matrixA = IntArray(totalElements) { it % 100 }
        matrixB = IntArray(totalElements) { (it * 2) % 100 }
        matrixC = IntArray(totalElements)
    }

    // 1. THE BASELINE: Standard Sequential Loop
    @Benchmark
    fun benchmarkSequential() {
        for (i in 0 until size) {
            for (j in 0 until size) {
                var sum = 0
                for (k in 0 until size) {
                    sum += matrixA[i * size + k] * matrixB[k * size + j]
                }
                matrixC[i * size + j] = sum
            }
        }
    }

    // 2. KOTLIN COROUTINES: The Idiomatic Kotlin Way
    @Benchmark
    fun benchmarkCoroutines() = runBlocking(Dispatchers.Default) {
        (0 until size).map { i ->
            async {
                for (j in 0 until size) {
                    var sum = 0
                    for (k in 0 until size) {
                        sum += matrixA[i * size + k] * matrixB[k * size + j]
                    }
                    matrixC[i * size + j] = sum
                }
            }
        }.awaitAll()
    }

    // 3. MANUAL FORKJOIN: The Boilerplate JVM Way
    @Benchmark
    fun benchmarkManualForkJoin() {
        val pool = ForkJoinPool.commonPool()
        val numThreads = pool.parallelism.coerceAtLeast(1)
        val cSize = (size + numThreads - 1) / numThreads

        val tasks = (0 until numThreads).mapNotNull { threadId ->
            val chunkStart = threadId * cSize
            if (chunkStart >= size) return@mapNotNull null

            val chunkEnd = minOf(chunkStart + cSize - 1, size - 1)

            Runnable {
                for (i in chunkStart..chunkEnd) {
                    for (j in 0 until size) {
                        var sum = 0
                        for (k in 0 until size) {
                            sum += matrixA[i * size + k] * matrixB[k * size + j]
                        }
                        matrixC[i * size + j] = sum
                    }
                }
            }
        }
        val futures = tasks.map { pool.submit(it) }
        futures.forEach { it.get() }
    }

    // 4. YOUR COMPILER PLUGIN: The OpenMP Way
    @Benchmark
    fun benchmarkKotlinMpDefault() {
        omp {
            parallelFor(0 until size) { i ->
                for (j in 0 until size) {
                    var sum = 0
                    for (k in 0 until size) {
                        sum += matrixA[i * size + k] * matrixB[k * size + j]
                    }
                    matrixC[i * size + j] = sum
                }
            }
        }
    }
}