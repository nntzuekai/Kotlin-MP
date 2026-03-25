package com.rkh.kotlinmp.tests

// Import your DSL library!
import com.rkh.kotlinmp.*

fun main() {
    println("--- Starting Kotlin-MP Sequential Test ---")
    
    val size = 20
    val a = IntArray(size) { it }       // [0, 1, 2, ...]
    val b = IntArray(size) { it * 2 }   // [0, 2, 4, ...]
    val c = IntArray(size)
    
    val sharedList = mutableListOf<Int>()

    // Your custom DSL in action!
    omp {
        parallelFor(0 until size, schedule = Schedule.Static) { i ->
            // Print which hardware thread is doing the math
            println("Index $i is being processed by: ${Thread.currentThread().name}")

            c[i] = a[i] + b[i]
            if (c[i] % 3 == 0) {
                critical {
                    sharedList.add(c[i])
                }
            }
        }
    }
    
    println("Math Array 'c' first 5 elements: ${c.take(5)}")
    println("Shared List (multiples of 3): $sharedList")
    println("--- Test Complete ---")
}