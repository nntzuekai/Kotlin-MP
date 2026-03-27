package com.rkh.kotlinmp.tests

import com.rkh.kotlinmp.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class ParallelBarrierTest {

    @Test
    fun testExplicitBarrierInsideOmp() {
        println("--- Running OpenMP Barrier Test (DSL Scope) ---")

        val threadCount = 4
        val phase1Completions = AtomicInteger(0)

        // We use an AtomicBoolean here because multiple threads might detect a breach
        // simultaneously, and standard booleans aren't thread-safe for writing.
        val barrierBreachDetected = AtomicBoolean(false)

        // 1. The Outer Scope
        omp {

            // 2. The General Parallel Region (spawns the team)
            parallel(numThreads = threadCount) {

                // --- PHASE 1 ---
                val threadName = Thread.currentThread().name
                println("Thread $threadName starting Phase 1")

                // We force the threads to sleep for random durations so they hit
                // the barrier at completely different times.
                Thread.sleep((Math.random() * 50).toLong() + 10)

                phase1Completions.incrementAndGet()
                println("Thread $threadName arrived at the barrier")

                // --- THE BARRIER ---
                // Halts the thread until the Phaser registers that all threads have arrived.
                barrier()

                // --- PHASE 2 ---
                println("Thread $threadName entered Phase 2")

                // If the barrier is mathematically sound, NO thread can physically execute
                // this line of code until `phase1Completions` is exactly equal to `threadCount`.
                if (phase1Completions.get() != threadCount) {
                    barrierBreachDetected.set(true)
                }
            }
        }

        // --- ASSERTIONS ---
        assertFalse(
            barrierBreachDetected.get(),
            "FATAL: A thread breached the barrier before all other threads arrived!"
        )
        assertEquals(
            threadCount,
            phase1Completions.get(),
            "Not all threads successfully registered their Phase 1 completion."
        )

        println("Explicit Barrier DSL Test Passed!\n")
    }
}