package com.rkh.kotlinmp

/**
 * The entry point for the Kotlin-MP DSL.
 */
inline fun omp(block: OmpContext.() -> Unit) {
    // We instantiate the context and apply the user's lambda to it.
    val context = OmpContext()
    context.block()
}