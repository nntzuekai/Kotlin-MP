plugins {
    kotlin("jvm")
}

group = "com.rkh.kotlinmp"
version = "1.0-SNAPSHOT"

kotlin { jvmToolchain(17) }

dependencies {
    // This is the "Magic" dependency! 
    // It gives you access to the internal Kotlin IR AST and Compiler APIs.
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.23")
}