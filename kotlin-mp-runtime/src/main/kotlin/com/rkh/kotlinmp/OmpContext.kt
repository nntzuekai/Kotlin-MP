package com.rkh.kotlinmp

import java.util.concurrent.ForkJoinPool

class OmpContext {

    /**
     * The Work-Sharing construct.
     * * NOTE: We use `inline` and `crossinline`. This is CRITICAL for your IR compiler.
     * It prevents Kotlin from creating anonymous `Function1` objects at compile time,
     * making the AST much easier for your IR plugin to read and transform.
     */
    // 1. Allowed: Default (No schedule)
    inline fun parallelFor(range: IntRange, crossinline block: (Int) -> Unit) {
        for (i in range) block(i)
    }

    // 2. Allowed: Explicit Subtypes
    inline fun parallelFor(range: IntRange, schedule: Schedule.Static, crossinline block: (Int) -> Unit) {
        for (i in range) block(i)
    }
    inline fun parallelFor(range: IntRange, schedule: Schedule.StaticChunked, crossinline block: (Int) -> Unit) {
        for (i in range) block(i)
    }
    inline fun parallelFor(range: IntRange, schedule: Schedule.Dynamic, crossinline block: (Int) -> Unit) {
        for (i in range) block(i)
    }
    inline fun parallelFor(range: IntRange, schedule: Schedule.DynamicChunked, crossinline block: (Int) -> Unit) {
        for (i in range) block(i)
    }

    // 3. THE POISON PILL: Outlaw generic variables
    @Deprecated(
        message = "OpenMP scheduling must be known at compile-time for zero-cost performance. Pass a specific schedule object (e.g., Schedule.Static) rather than a variable.",
        level = DeprecationLevel.ERROR
    )
    inline fun parallelFor(range: IntRange, schedule: Schedule, crossinline block: (Int) -> Unit) {
        error("This should never execute because compilation will fail.")
    }

    // 1. Allowed: Default (No schedule)
    inline fun parallelFor(range: IntProgression, crossinline block: (Int) -> Unit) {
        for (i in range) block(i)
    }

    // 2. Allowed: Explicit Subtypes
    inline fun parallelFor(range: IntProgression, schedule: Schedule.Static, crossinline block: (Int) -> Unit) {
        for (i in range) block(i)
    }
    inline fun parallelFor(range: IntProgression, schedule: Schedule.StaticChunked, crossinline block: (Int) -> Unit) {
        for (i in range) block(i)
    }
    inline fun parallelFor(range: IntProgression, schedule: Schedule.Dynamic, crossinline block: (Int) -> Unit) {
        for (i in range) block(i)
    }
    inline fun parallelFor(range: IntProgression, schedule: Schedule.DynamicChunked, crossinline block: (Int) -> Unit) {
        for (i in range) block(i)
    }

    // 3. THE POISON PILL: Outlaw generic variables
    @Deprecated(
        message = "OpenMP scheduling must be known at compile-time for zero-cost performance. Pass a specific schedule object (e.g., Schedule.Static) rather than a variable.",
        level = DeprecationLevel.ERROR
    )
    inline fun parallelFor(range: IntProgression, schedule: Schedule, crossinline block: (Int) -> Unit) {
        error("This should never execute because compilation will fail.")
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
fun executeParallelRangeStatic(range: IntRange, schedule: Schedule.Static, block: (Int) -> Unit) {
    if (range.isEmpty()) return

    val pool = ForkJoinPool.commonPool()
    // equivalent to maxOf(pool.parallelism, 1)
    val numThreads = pool.parallelism.coerceAtLeast(1)
    
    val start = range.first
    val endInclusive = range.last
    val totalElements = endInclusive - start + 1

    // equivalent to Math.ceil(1.0*totalElements/numThreads)
    val cSize = (totalElements + numThreads - 1) / numThreads

    val tasks = (0 until numThreads).mapNotNull { threadId ->
        val chunkStart = start + (threadId * cSize)
        if (chunkStart > endInclusive) null // Thread has no work
        else {
            val chunkEnd = minOf(chunkStart + cSize - 1, endInclusive)
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

fun executeParallelRangeStaticChunked(range: IntRange, schedule: Schedule.StaticChunked, block: (Int) -> Unit) {
    if (range.isEmpty()) return

    val pool = ForkJoinPool.commonPool()
    // equivalent to maxOf(pool.parallelism, 1)
    val numThreads = pool.parallelism.coerceAtLeast(1)

    val start = range.first
    val endInclusive = range.last
    val cSize = schedule.chunkSize

    val tasks = (0 until numThreads).mapNotNull { threadId ->
        Runnable {
            var currentChunkIndex = threadId
            while (true) {
                val chunkStart = start + (currentChunkIndex * cSize)
                if (chunkStart > endInclusive) break

                val chunkEnd = minOf(chunkStart + cSize - 1, endInclusive)
                for (i in chunkStart..chunkEnd) block(i)

                currentChunkIndex += numThreads
            }
        }
    }

    // Submit all chunks to the hardware threads and wait for them to finish
    val futures = tasks.map { pool.submit(it) }
    futures.forEach { it.get() }
}

fun executeParallelProgressionStatic(progression: IntProgression, schedule: Schedule.Static, block: (Int) -> Unit) {
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
    val cSize = (totalElements + numThreads - 1) / numThreads

    val tasks = (0 until numThreads).mapNotNull { threadId ->
        // We are now calculating the STARTING INDEX, not the starting value
        val chunkStartIndex = threadId * cSize

        if (chunkStartIndex >= totalElements) null // Thread has no work
        else {
            val chunkEndIndex = minOf(chunkStartIndex + cSize - 1, totalElements - 1)
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

fun executeParallelProgressionStaticChunked(progression: IntProgression, schedule: Schedule.StaticChunked, block: (Int) -> Unit) {
    if (progression.isEmpty()) return

    val pool = ForkJoinPool.commonPool()
    // equivalent to maxOf(pool.parallelism, 1)
    val numThreads = pool.parallelism.coerceAtLeast(1)

    val first = progression.first
    val last = progression.last
    val step = progression.step

    // Calculate total number of iterations regardless of positive/negative step
    val totalElements = ((last - first) / step) + 1
    val cSize = schedule.chunkSize

    val tasks =  (0 until numThreads).mapNotNull { threadId ->
        Runnable {
            var currentChunkIndex = threadId

            while (true) {
                val chunkStartIndex = currentChunkIndex * cSize
                if (chunkStartIndex >= totalElements) break // We ran out of array to deal!

                val chunkEndIndex = minOf(chunkStartIndex + cSize - 1, totalElements - 1)
                for (i in chunkStartIndex..chunkEndIndex) {
                    val actualValue = first + (i * step)
                    block(actualValue)
                }

                // Jump forward by the number of threads to grab the next round-robin chunk
                currentChunkIndex += numThreads
            }
        }
    }

    // Submit all chunks to the hardware threads and wait for them to finish
    val futures = tasks.map { pool.submit(it) }
    futures.forEach { it.get() }
}