package com.rkh.kotlinmp

sealed class Schedule {
    // 1. The Default Instance (Allows: Schedule.Static)
    object Static : Schedule() {

        // 2. The Overload (Allows: Schedule.Static(2))
        // When the user adds parentheses, Kotlin secretly calls this function!
        operator fun invoke(chunkSize: Int = 0): Schedule {
            return CustomStatic(chunkSize)
        }
    }

    // 3. The Data Holder for custom chunk sizes (Hidden from the user)
    data class CustomStatic(val chunkSize: Int) : Schedule()

    /** * Threads pull chunks of work from a shared atomic counter dynamically.
     * @param chunkSize How many iterations a thread grabs at once.
     */
    data class Dynamic(val chunkSize: Int = 1) : Schedule()
}