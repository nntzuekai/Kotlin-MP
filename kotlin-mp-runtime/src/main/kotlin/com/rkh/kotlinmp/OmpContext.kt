package com.rkh.kotlinmp

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import java.util.concurrent.Phaser

@DslMarker
annotation class OpenMpDsl

@OpenMpDsl
class ParallelScope(@PublishedApi internal val phaser: Phaser) {

    /**
     * OpenMP Explicit Barrier.
     * Only available inside `parallel { ... }`.
     */
    inline fun barrier() {
        phaser.arriveAndAwaitAdvance()
    }
}

@OpenMpDsl
class OmpContext {

    /**
     * The Work-Sharing construct.
     * * NOTE: We use `inline` and `crossinline`. This is CRITICAL for your IR compiler.
     * It prevents Kotlin from creating anonymous `Function1` objects at compile time,
     * making the AST much easier for your IR plugin to read and transform.
     */
    // 1. Allowed: Default (No schedule)
    inline fun parallelFor(range: IntRange, crossinline block: (Int) -> Unit) {
        parallelFor(range, Schedule.Static, block)
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
    inline fun parallelFor(progression: IntProgression, crossinline block: (Int) -> Unit) {
        parallelFor(progression, Schedule.Static(),block)
    }

    // 2. Allowed: Explicit Subtypes
    inline fun parallelFor(progression: IntProgression, schedule: Schedule.Static, crossinline block: (Int) -> Unit) {
        for (i in progression) block(i)
    }
    inline fun parallelFor(progression: IntProgression, schedule: Schedule.StaticChunked, crossinline block: (Int) -> Unit) {
        for (i in progression) block(i)
    }
    inline fun parallelFor(progression: IntProgression, schedule: Schedule.Dynamic, crossinline block: (Int) -> Unit) {
        for (i in progression) block(i)
    }
    inline fun parallelFor(progression: IntProgression, schedule: Schedule.DynamicChunked, crossinline block: (Int) -> Unit) {
        for (i in progression) block(i)
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
    companion object {
        // 1. The Global Lock (For unnamed `#pragma omp critical`)
        @PublishedApi
        internal val defaultCriticalLock = ReentrantLock()

        // 2. The Lock Registry (For named `#pragma omp critical(name)`)
        @PublishedApi
        internal val namedCriticalLocks = ConcurrentHashMap<String, ReentrantLock>()
    }

    /**
     * OpenMP Unnamed Critical Section.
     * Ensures mutual exclusion across ALL unnamed critical sections globally.
     */
    inline fun critical(crossinline block: () -> Unit) {
        defaultCriticalLock.withLock {
            block()
        }
    }

    /**
     * OpenMP Named Critical Section.
     * Ensures mutual exclusion only among critical sections sharing this exact name.
     */
    inline fun critical(name: String, crossinline block: () -> Unit) {
        // getOrPut is thread-safe thanks to ConcurrentHashMap
        val lock = namedCriticalLocks.getOrPut(name) { ReentrantLock() }
        lock.withLock {
            block()
        }
    }

    /**
     * OpenMP General Parallel Region.
     * Spawns threads and changes the receiver to `ParallelScope` so `barrier()` becomes legal.
     */
    inline fun parallel(numThreads: Int = ForkJoinPool.commonPool().parallelism.coerceAtLeast(1), crossinline block: ParallelScope.() -> Unit) {
        if (numThreads <= 0) return
        val pool = ForkJoinPool.commonPool()
        val phaser = Phaser(numThreads)
        val scope = ParallelScope(phaser)

        val tasks = (0 until numThreads).map {
            Runnable {
                try {
                    scope.block()
                } finally {
                    phaser.arriveAndDeregister()
                }
            }
        }
        val futures = tasks.map { pool.submit(it) }
        futures.forEach { it.get() }
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

    val tasks = (0 until numThreads).map { threadId ->
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

    val tasks =  (0 until numThreads).map { threadId ->
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

fun executeParallelRangeDynamicDefault(range: IntRange, schedule: Schedule.Dynamic, block: (Int) -> Unit) {
    if (range.isEmpty()) return
    val pool = ForkJoinPool.commonPool()
    val numThreads = pool.parallelism.coerceAtLeast(1)

    val start = range.first
    val endInclusive = range.last
    // The shared, thread-safe hardware counter
    val sharedIndex = AtomicInteger(start)

    val tasks = (0 until numThreads).map {
        Runnable {
            while (true) {
                // Atomically claim the exact next index
                val currentIndex = sharedIndex.getAndIncrement()

                // If the index we pulled is out of bounds, the pile is empty!
                if (currentIndex > endInclusive) break

                // Execute the math
                block(currentIndex)
            }
        }
    }
    val futures = tasks.map { pool.submit(it) }
    futures.forEach { it.get() }
}

fun executeParallelRangeDynamicChunked(range: IntRange, schedule: Schedule.DynamicChunked, block: (Int) -> Unit) {
    if (range.isEmpty()) return
    val pool = ForkJoinPool.commonPool()
    val numThreads = pool.parallelism.coerceAtLeast(1)

    val start = range.first
    val endInclusive = range.last
    val cSize = schedule.chunkSize // Guaranteed to be >= 1 by the class init block

    val sharedIndex = AtomicInteger(start)

    val tasks = (0 until numThreads).map {
        Runnable {
            while (true) {
                // Atomically claim a chunk of work
                val chunkStart = sharedIndex.getAndAdd(cSize)

                if (chunkStart > endInclusive) break

                // Calculate where this specific chunk ends
                val chunkEnd = minOf(chunkStart + cSize - 1, endInclusive)

                // Process the claimed chunk
                for (i in chunkStart..chunkEnd) {
                    block(i)
                }
            }
        }
    }
    val futures = tasks.map { pool.submit(it) }
    futures.forEach { it.get() }
}

fun executeParallelProgressionDynamicDefault(progression: IntProgression, schedule: Schedule.Dynamic, block: (Int) -> Unit) {
    if (progression.isEmpty()) return
    val pool = ForkJoinPool.commonPool()
    val numThreads = pool.parallelism.coerceAtLeast(1)

    // 1. Loop Normalization: Extract the physical layout
    val first = progression.first
    val last = progression.last
    val step = progression.step

    // Kotlin's IntProgression guarantees that `last` is exactly the final element hit,
    // so this division will always yield a perfect integer count.
    val totalElements = ((last - first) / step) + 1

    // 2. The Shared Logical Counter
    val sharedLogicalIndex = AtomicInteger(0)

    val tasks = (0 until numThreads).map {
        Runnable {
            while (true) {
                // Steal the next logical index
                val logicalIndex = sharedLogicalIndex.getAndIncrement()

                if (logicalIndex >= totalElements) break

                // Map the logical index back to the physical value
                val actualValue = first + (logicalIndex * step)
                block(actualValue)
            }
        }
    }
    val futures = tasks.map { pool.submit(it) }
    futures.forEach { it.get() }
}

fun executeParallelProgressionDynamicChunked(progression: IntProgression, schedule: Schedule.DynamicChunked, block: (Int) -> Unit) {
    if (progression.isEmpty()) return
    val pool = ForkJoinPool.commonPool()
    val numThreads = pool.parallelism.coerceAtLeast(1)

    val first = progression.first
    val step = progression.step
    val last = progression.last
    val totalElements = ((last - first) / step) + 1

    val cSize = schedule.chunkSize // Guaranteed >= 1
    val sharedLogicalIndex = AtomicInteger(0)

    val tasks = (0 until numThreads).map {
        Runnable {
            while (true) {
                // Steal a chunk of logical indices
                val chunkStartLogical = sharedLogicalIndex.getAndAdd(cSize)

                if (chunkStartLogical >= totalElements) break

                val chunkEndLogical = minOf(chunkStartLogical + cSize - 1, totalElements - 1)

                // Process the claimed chunk
                for (logicalIndex in chunkStartLogical..chunkEndLogical) {
                    val actualValue = first + (logicalIndex * step)
                    block(actualValue)
                }
            }
        }
    }
    val futures = tasks.map { pool.submit(it) }
    futures.forEach { it.get() }
}