plugins {
    kotlin("jvm")
    id("application")
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
}

application {
    mainClass.set("com.rkh.kotlinmp.tests.MainKt")
}

// 3. The Compiler Hook: Inject the plugin during the compile task
// tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//     // Ensure the compiler module is built BEFORE we try to compile the tests
//     dependsOn(myCompilerPlugin) 
    
//     doFirst {
//         // Find the compiled JAR file of your compiler plugin
//         val pluginJars = myCompilerPlugin.files.map { "-Xplugin=${it.absolutePath}" }
//         // Pass it to the Kotlin compiler
//         kotlinOptions.freeCompilerArgs += pluginJars
//     }
// }