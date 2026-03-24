plugins {
    // Applies the Kotlin JVM plugin to compile standard Kotlin code
    kotlin("jvm") version "1.9.23" 
}

group = "com.rkh.kotlinmp"
version = "1.0-SNAPSHOT"


// Configure the Kotlin compiler
kotlin {
    // Use Java 17 (the current enterprise LTS standard)
    jvmToolchain(17) 
}

dependencies {
    // Notice how empty this is! 
    // You only need the Kotlin Standard Library, which the plugin includes automatically.
    // If you were to write unit tests for this specific module later, 
    // you would add JUnit dependencies here.
}