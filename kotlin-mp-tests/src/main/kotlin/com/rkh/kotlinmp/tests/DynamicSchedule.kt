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
        parallelFor(0 until size, schedule = Schedule.Dynamic(chunkSize = 5)) { i ->
            // 1. Math calculation
            c[i] = a[i] + b[i]
            
            // 2. Safe shared mutation
            if (c[i] % 3 == 0) {
                critical {
                    sharedList.add(c[i])
                }
            }
        }
        
        barrier()
    }
    
    println("Math Array 'c' first 5 elements: ${c.take(5)}")
    println("Shared List (multiples of 3): $sharedList")
    println("--- Test Complete ---")
}