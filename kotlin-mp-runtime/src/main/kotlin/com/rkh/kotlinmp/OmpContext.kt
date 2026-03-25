package com.rkh.kotlinmp

import java.util.concurrent.ForkJoinPool

class OmpContext {

    /**
     * The Work-Sharing construct.
     * * NOTE: We use `inline` and `crossinline`. This is CRITICAL for your IR compiler.
     * It prevents Kotlin from creating anonymous `Function1` objects at compile time,
     * making the AST much easier for your IR plugin to read and transform.
     */
    // Fast Path: For standard ranges (0 until 20)
    inline fun parallelFor(
        range: IntRange,
        schedule: Schedule = Schedule.Static,
        crossinline block: (Int) -> Unit
    ) {
        // SEQUENTIAL FALLBACK: 
        // If the compiler plugin is disabled, this just runs a normal for-loop.
        // Your compiler plugin will DELETE this code and replace it with ForkJoinPool logic.
        for (i in range) {
            block(i)
        }
    }

    // Slow Path: For custom steps (20 downTo 0 step 2)
    inline fun parallelFor(
        progression: IntProgression,
        schedule: Schedule = Schedule.Static,
        crossinline block: (Int) -> Unit
    ) {
        for (i in progression) {
            block(i)
        }
    }

    /**
     * The Mutual Exclusion construct.
     */
    inline fun critical(block: () -> Unit) {
        // SEQUENTIAL FALLBACK:
        // Since sequential code only has one thread, no locking is needed.
        // Your compiler plugin will wrap this in a JVM Monitor (synchronized).
        block()
    }

    /**
     * The Synchronization construct.
     */
    fun barrier() {
        // SEQUENTIAL FALLBACK:
        // A barrier with 1 thread is a no-op.
        // Your compiler plugin will replace this with `CyclicBarrier.await()`.
    }
}

/**
 * This is the HIDDEN support function. 
 * The user never types this. The compiler plugin rewrites their code to call this instead!
 */
fun executeParallelRangeStatic(range: IntRange, block: (Int) -> Unit) {
    val pool = ForkJoinPool.commonPool()
    // equivalent to maxOf(pool.parallelism, 1)
    val numThreads = pool.parallelism.coerceAtLeast(1)
    
    val start = range.first
    val endInclusive = range.last
    val totalElements = endInclusive - start + 1
    
    if (totalElements <= 0) return

    // equivalent to Math.ceil(1.0*totalElements/numThreads)
    val chunkSize = (totalElements + numThreads - 1) / numThreads

    val tasks = (0 until numThreads).mapNotNull { threadId ->
        val chunkStart = start + (threadId * chunkSize)
        if (chunkStart > endInclusive) null // Thread has no work
        else {
            val chunkEnd = minOf(chunkStart + chunkSize - 1, endInclusive)
            Runnable {
                // This is where the user's original math gets executed!
                for (i in chunkStart..chunkEnd) {
                    block(i)
                }
            }
        }
    }
    
    // Submit all chunks to the hardware threads and wait for them to finish
    val futures = tasks.map { pool.submit(it) }
    futures.forEach { it.get() }
}

fun executeParallelProgressionStatic(progression: IntProgression, block: (Int) -> Unit) {
    if (progression.isEmpty()) return

    val pool = ForkJoinPool.commonPool()
    // equivalent to maxOf(pool.parallelism, 1)
    val numThreads = pool.parallelism.coerceAtLeast(1)

    val first = progression.first
    val last = progression.last
    val step = progression.step

    // Calculate total number of iterations regardless of positive/negative step
    val totalElements = ((last - first) / step) + 1

    // equivalent to Math.ceil(1.0*totalElements/numThreads)
    val chunkSize = (totalElements + numThreads - 1) / numThreads

    val tasks = (0 until numThreads).mapNotNull { threadId ->
        // We are now calculating the STARTING INDEX, not the starting value
        val chunkStartIndex = threadId * chunkSize

        if (chunkStartIndex >= totalElements) null // Thread has no work
        else {
            val chunkEndIndex = minOf(chunkStartIndex + chunkSize - 1, totalElements - 1)
            Runnable {
                // Iterate through the assigned indices
                for (i in chunkStartIndex..chunkEndIndex) {
                    // Map the index back to the actual progression value
                    val actualValue = first + (i * step)
                    block(actualValue)
                }
            }
        }
    }
    
    // Submit all chunks to the hardware threads and wait for them to finish
    val futures = tasks.map { pool.submit(it) }
    futures.forEach { it.get() }
}