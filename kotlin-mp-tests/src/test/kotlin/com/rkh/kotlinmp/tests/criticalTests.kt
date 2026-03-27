package com.rkh.kotlinmp.tests

import com.rkh.kotlinmp.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class criticalTests {
    @Test
    fun testCriticalMutualExclusion() {
        println("--- Running OpenMP Critical Construct Test ---")
        val size = 100_000

        var unprotectedCounter = 0
        var protectedCounter = 0
        var namedProtectedCounter = 0

        omp {
            // We use Dynamic to aggressively force thread collisions
            parallelFor(0 until size, Schedule.Dynamic(100)) {

                // 1. The Data Race (Will lose counts due to thread collisions)
                unprotectedCounter++

                // 2. Unnamed Critical (Perfectly safe)
                critical {
                    protectedCounter++
                }

                // 3. Named Critical (Perfectly safe, independent lock)
                critical("CounterLock") {
                    namedProtectedCounter++
                }
            }
        }

        println("Unprotected Counter (Lost Updates): $unprotectedCounter")
        println("Protected Counter (Critical): $protectedCounter")
        println("Named Protected Counter: $namedProtectedCounter")

        // Assertions
        assertEquals(size, protectedCounter, "Unnamed critical section failed mutual exclusion!")
        assertEquals(size, namedProtectedCounter, "Named critical section failed mutual exclusion!")

        // The unprotected counter should almost certainly be less than 'size'
        // because we just subjected it to a massive data race!
        assert(unprotectedCounter <= size)

        println("Critical Mutual Exclusion Test Passed!\n")
    }
}