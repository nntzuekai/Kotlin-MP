package com.rkh.kotlinmp.tests

import com.rkh.kotlinmp.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Collections

class DynamicScheduleTests {
    // Proving our IR Transformer successfully unwraps property getters!
    companion object {
        const val DYNAMIC_CHUNK_SIZE = 3
    }

    @Test
    fun testRangeDynamicDefault() {
        println("--- Running Dynamic Default (Range, Chunk = 1) ---")
        val size = 20
        val c = IntArray(size)

        // Using the empty parentheses invoke() operator
        omp {
            parallelFor(0 until size, Schedule.Dynamic()) { i ->
                println("Dynamic Default Index $i processed by: ${Thread.currentThread().name}")
                c[i] = i * 10
            }
        }

        for (i in 0 until size) {
            assertEquals(i * 10, c[i], "Index $i calculation failed")
        }
        println("Range Dynamic Default Test Passed!\n")
    }

    @Test
    fun testRangeDynamicChunkedWithConst1() {
        println("--- Running Dynamic Chunked (Range, Chunk = 1) ---")
        val size = 20
        val c = IntArray(size)

        // Passing the const val to test the IR extraction logic
        omp {
            parallelFor(0 until size, Schedule.Dynamic(1)) { i ->
                c[i] = i * 2
            }
        }

        for (i in 0 until size) {
            assertEquals(i * 2, c[i], "Chunked computation failed at $i")
        }
        println("Range Dynamic Chunked Test Passed!\n")
    }

    @Test
    fun testRangeDynamicChunkedWithConst() {
        println("--- Running Dynamic Chunked (Range, Chunk = $DYNAMIC_CHUNK_SIZE) ---")
        val size = 20
        val c = IntArray(size)

        // Passing the const val to test the IR extraction logic
        omp {
            parallelFor(0 until size, Schedule.Dynamic(DYNAMIC_CHUNK_SIZE)) { i ->
                c[i] = i * 2
            }
        }

        for (i in 0 until size) {
            assertEquals(i * 2, c[i], "Chunked computation failed at $i")
        }
        println("Range Dynamic Chunked Test Passed!\n")
    }

    @Test
    fun testProgressionDynamicChunked() {
        println("--- Running Dynamic Chunked (Progression, step = -2) ---")
        val size = 20
        val a = IntArray(size) { it }
        val b = IntArray(size) { it * 2 }
        val c = IntArray(size)

        // The progression: 18, 16, 14, 12, 10, 8, 6, 4, 2, 0 (Total 10 elements)
        omp {
            parallelFor(18 downTo 0 step 2, Schedule.Dynamic(2)) { i ->
                println("Progression Value $i processed by: ${Thread.currentThread().name}")
                // Simulating unbalanced workload to force work-stealing
                if (i == 14) Thread.sleep(50)

                c[i] = a[i] + b[i]
            }
        }

        // 1. Verification of the processed indices
        assertEquals(54, c[18], "c[18] should be 18 + 36")
        assertEquals(42, c[14], "c[14] should be 14 + 28")
        assertEquals(30, c[10], "c[10] should be 10 + 20")
        assertEquals(0, c[0], "c[0] should be 0 + 0")

        // 2. Verification of loop normalization (Bounds Safety)
        assertEquals(0, c[19], "Index 19 was skipped, must remain 0")
        assertEquals(0, c[17], "Index 17 was skipped, must remain 0")
        assertEquals(0, c[1], "Index 1 was skipped, must remain 0")

        println("Progression Dynamic Chunked Test Passed!\n")
    }
}