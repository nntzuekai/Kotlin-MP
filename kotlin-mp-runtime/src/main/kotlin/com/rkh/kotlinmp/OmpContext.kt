package com.rkh.kotlinmp

class OmpContext {

    /**
     * The Work-Sharing construct.
     * * NOTE: We use `inline` and `crossinline`. This is CRITICAL for your IR compiler.
     * It prevents Kotlin from creating anonymous `Function1` objects at compile time,
     * making the AST much easier for your IR plugin to read and transform.
     */
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