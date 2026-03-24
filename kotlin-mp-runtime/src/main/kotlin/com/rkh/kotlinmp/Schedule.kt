package com.rkh.kotlinmp

sealed class Schedule {
    /** * Divides the loop into chunks of (Total / Threads).
     * The compiler will calculate this math statically.
     */
    object Static : Schedule()

    /** * Threads pull chunks of work from a shared atomic counter dynamically.
     * @param chunkSize How many iterations a thread grabs at once.
     */
    data class Dynamic(val chunkSize: Int = 1) : Schedule()
}