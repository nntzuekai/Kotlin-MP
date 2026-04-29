package com.rkh.kotlinmp.benchmark

import com.rkh.kotlinmp.Schedule
import com.rkh.kotlinmp.omp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
open class SchedulerOverheadBenchmark {

    @Param("250", "750", "1500", "4000", "12000", "65000", "250000", "1000000")
    var size: Int = 0

    lateinit var input: IntArray
    lateinit var output: IntArray

    @Setup(Level.Trial)
    fun setup() {
        input = IntArray(size) { it }
        output = IntArray(size)
    }

    private fun lightweightKernel(index: Int) {
        val value = input[index]
        output[index] = (value xor (value shl 1)) + 1
    }

    @Benchmark
    fun sequential() {
        for (i in 0 until size) {
            lightweightKernel(i)
        }
    }

    @Benchmark
    fun coroutines() = runBlocking(Dispatchers.Default) {
        (0 until size).map { i ->
            async {
                lightweightKernel(i)
            }
        }.awaitAll()
    }

    @Benchmark
    fun manualForkJoinStatic() {
        val pool = ForkJoinPool.commonPool()
        val numThreads = pool.parallelism.coerceAtLeast(1)
        val chunkSize = (size + numThreads - 1) / numThreads

        val tasks = (0 until numThreads).mapNotNull { threadId ->
            val start = threadId * chunkSize
            if (start >= size) null
            else {
                val endExclusive = minOf(start + chunkSize, size)
                Runnable {
                    for (i in start until endExclusive) {
                        lightweightKernel(i)
                    }
                }
            }
        }
        val futures = tasks.map { pool.submit(it) }
        futures.forEach { it.get() }
    }

    @Benchmark
    fun manualForkJoinDynamic() {
        val pool = ForkJoinPool.commonPool()
        val numThreads = pool.parallelism.coerceAtLeast(1)
        val nextIndex = AtomicInteger(0)

        val tasks = (0 until numThreads).map {
            Runnable {
                while (true) {
                    val index = nextIndex.getAndIncrement()
                    if (index >= size) break
                    lightweightKernel(index)
                }
            }
        }
        val futures = tasks.map { pool.submit(it) }
        futures.forEach { it.get() }
    }

    @Benchmark
    fun kotlinMpStatic() {
        omp {
            parallelFor(0 until size, Schedule.Static) { i ->
                lightweightKernel(i)
            }
        }
    }

    @Benchmark
    fun kotlinMpStaticChunked() {
        omp {
            parallelFor(0 until size, Schedule.Static(1)) { i ->
                lightweightKernel(i)
            }
        }
    }

    @Benchmark
    fun kotlinMpDynamic() {
        omp {
            parallelFor(0 until size, Schedule.Dynamic) { i ->
                lightweightKernel(i)
            }
        }
    }

    @Benchmark
    fun kotlinMpDynamicChunked() {
        omp {
            parallelFor(0 until size, Schedule.Dynamic(16)) { i ->
                lightweightKernel(i)
            }
        }
    }
}
