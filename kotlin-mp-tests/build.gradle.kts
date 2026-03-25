plugins {
    kotlin("jvm")
    kotlin("kapt") // 1. Added Kapt for JMH annotation processing
    id("me.champeau.jmh") version "0.7.2" // 2. The official JMH plugin for Gradle
}

group = "com.rkh.kotlinmp"
version = "1.0-SNAPSHOT"

kotlin { jvmToolchain(17) }

// Create a custom Gradle configuration to hold our compiler plugin
val myCompilerPlugin by configurations.creating

dependencies {
    // 1. Give the tests access to your DSL (omp, parallelFor)
    implementation(project(":kotlin-mp-runtime"))
    
    // 2. Tell Gradle that our custom configuration depends on the compiler module
    myCompilerPlugin(project(":kotlin-mp-compiler"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
}

tasks.withType<Test> {
    useJUnitPlatform()

    // This ensures our standard output (printlns) shows up in the terminal during tests
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
    }
}

jmh {
    warmupIterations.set(3)
    iterations.set(5)
    fork.set(1) // Run in a single forked JVM to save time during testing
    threads.set(1) // JMH threads, NOT your ForkJoinPool threads! Keep this at 1.
}


// 3. The Compiler Hook: Inject the plugin during the compile task
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    dependsOn(myCompilerPlugin)
    // We use the modern 'compilerOptions' API and a lazy 'provider'
    compilerOptions {
        freeCompilerArgs.addAll(
            provider {
                myCompilerPlugin.files.map { "-Xplugin=${it.absolutePath}" }
            }
        )
    }
}