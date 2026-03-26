package com.rkh.kotlinmp.tests

import com.rkh.kotlinmp.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Collections

const val CHUNK_SIZE=2

class StaticScheduleTests {
    companion object{
        const val CHUNK_SIZE1=2
    }

    @Test
    fun testProgression() {
        println("--- Running Slow Path (IntProgression) Test ---")
        val size = 20
        val a = IntArray(size) { it }
        val b = IntArray(size) { it * 2 }
        val c = IntArray(size)

        // This should trigger executeParallelProgressionStatic
        omp {
            parallelFor(18 downTo 0 step 2, Schedule.Static()) { i ->
                println("Progression Index $i processed by: ${Thread.currentThread().name}")
                c[i] = a[i] + b[i]
            }
        }

        // Mathematical verification
        assertEquals(0, c[0])
        assertEquals(6, c[2])
        assertEquals(54, c[18])

        // Ensure skipped indices were NOT modified (they should remain 0)
        assertEquals(0, c[1], "Index 1 was skipped, should be 0")
        assertEquals(0, c[19], "Index 19 was skipped, should be 0")

        println("Slow Path Test Passed!\n")
    }

    @Test
    fun testStandardRange() {
        println("--- Running Fast Path (IntRange) Test ---")
        val size = 20
        val a = IntArray(size) { it }
        val b = IntArray(size) { it * 2 }
        val c = IntArray(size)

        // The synchronized list ensures thread-safe adds during testing
        val sharedList = Collections.synchronizedList(mutableListOf<Int>())

        // This should trigger executeParallelRangeStatic
        omp {
            parallelFor(0 until size, Schedule.Static) { i ->
                println("Range Index $i processed by: ${Thread.currentThread().name}")
                c[i] = a[i] + b[i]
                if (c[i] % 3 == 0) sharedList.add(c[i])
            }
        }

        // Mathematical verification
        assertEquals(0, c[0], "c[0] should be 0 + 0")
        assertEquals(3, c[1], "c[1] should be 1 + 2")
        assertEquals(57, c[19], "c[19] should be 19 + 38")

        println("Fast Path Test Passed!\n")
    }

    @Test
    fun testProgressionCyclicChunking() {
        println("--- Running Slow Path (Progression) + Round-Robin (Chunk Size = 2) ---")
        val size = 20
        val a = IntArray(size) { it }
        val b = IntArray(size) { it * 2 }
        val c = IntArray(size)

        // The progression: 18, 16, 14, 12, 10, 8, 6, 4, 2, 0 (Total 10 elements)
        // With Schedule.Static(2), threads will grab 2 elements at a time,
        // then jump forward cyclically.
        omp {
            parallelFor(18 downTo 0 step 2, Schedule.Static(2)) { i ->
                println("Progression Value $i processed by: ${Thread.currentThread().name}")
                c[i] = a[i] + b[i]
            }
        }

        // 1. Mathematical verification of the processed indices
        assertEquals(54, c[18], "c[18] should be 18 + 36")
        assertEquals(42, c[14], "c[14] should be 14 + 28")
        assertEquals(30, c[10], "c[10] should be 10 + 20")
        assertEquals(0, c[0], "c[0] should be 0 + 0")

        // 2. Strict verification that the cyclic math didn't bleed into adjacent memory
        assertEquals(0, c[19], "Index 19 was skipped, must remain 0")
        assertEquals(0, c[17], "Index 17 was skipped, must remain 0")
        assertEquals(0, c[1], "Index 1 was skipped, must remain 0")

        println("Progression Round-Robin Test Passed!\n")
    }


    @Test
    fun testProgressionCyclicChunkingConstVal() {
        println("--- Running Slow Path (Progression) + Round-Robin (Chunk Size = 2) ---")
        val size = 20
        val a = IntArray(size) { it }
        val b = IntArray(size) { it * 2 }
        val c = IntArray(size)

        // The progression: 18, 16, 14, 12, 10, 8, 6, 4, 2, 0 (Total 10 elements)
        // With Schedule.Static(2), threads will grab 2 elements at a time,
        // then jump forward cyclically.
        omp {
            parallelFor(18 downTo 0 step 2, Schedule.Static(CHUNK_SIZE)) { i ->
                println("Progression Value $i processed by: ${Thread.currentThread().name}")
                c[i] = a[i] + b[i]
            }
        }

        // 1. Mathematical verification of the processed indices
        assertEquals(54, c[18], "c[18] should be 18 + 36")
        assertEquals(42, c[14], "c[14] should be 14 + 28")
        assertEquals(30, c[10], "c[10] should be 10 + 20")
        assertEquals(0, c[0], "c[0] should be 0 + 0")

        // 2. Strict verification that the cyclic math didn't bleed into adjacent memory
        assertEquals(0, c[19], "Index 19 was skipped, must remain 0")
        assertEquals(0, c[17], "Index 17 was skipped, must remain 0")
        assertEquals(0, c[1], "Index 1 was skipped, must remain 0")

        println("Progression Round-Robin Test Passed!\n")
    }

    @Test
    fun testProgressionCyclicChunkingConstVal2() {
        println("--- Running Slow Path (Progression) + Round-Robin (Chunk Size = 2) ---")
        val size = 20
        val a = IntArray(size) { it }
        val b = IntArray(size) { it * 2 }
        val c = IntArray(size)

        // The progression: 18, 16, 14, 12, 10, 8, 6, 4, 2, 0 (Total 10 elements)
        // With Schedule.Static(2), threads will grab 2 elements at a time,
        // then jump forward cyclically.
        omp {
            parallelFor(18 downTo 0 step 2, Schedule.Static(CHUNK_SIZE1)) { i ->
                println("Progression Value $i processed by: ${Thread.currentThread().name}")
                c[i] = a[i] + b[i]
            }
        }

        // 1. Mathematical verification of the processed indices
        assertEquals(54, c[18], "c[18] should be 18 + 36")
        assertEquals(42, c[14], "c[14] should be 14 + 28")
        assertEquals(30, c[10], "c[10] should be 10 + 20")
        assertEquals(0, c[0], "c[0] should be 0 + 0")

        // 2. Strict verification that the cyclic math didn't bleed into adjacent memory
        assertEquals(0, c[19], "Index 19 was skipped, must remain 0")
        assertEquals(0, c[17], "Index 17 was skipped, must remain 0")
        assertEquals(0, c[1], "Index 1 was skipped, must remain 0")

        println("Progression Round-Robin Test Passed!\n")
    }
}