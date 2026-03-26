package com.rkh.kotlinmp

sealed class Schedule {

    // --- STATIC VARIANTS ---
    object Static : Schedule() {
        operator fun invoke(): Static = this
        operator fun invoke(chunkSize: Int) = StaticChunked(chunkSize)
    }

    class StaticChunked(val chunkSize: Int) : Schedule() {
        init {
            require(chunkSize >= 1) { "OpenMP requirement: chunkSize must be >= 1" }
        }
    }

    // --- DYNAMIC VARIANTS ---
    object Dynamic : Schedule() {
        operator fun invoke(): Dynamic = this
        operator fun invoke(chunkSize: Int) = DynamicChunked(chunkSize)
    }

    class DynamicChunked(val chunkSize: Int) : Schedule() {
        init {
            require(chunkSize >= 1) { "OpenMP requirement: chunkSize must be >= 1" }
        }
    }
}