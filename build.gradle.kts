plugins {
    // We declare the Kotlin plugin here, but "apply false" means 
    // we only apply it to the sub-modules, not the root folder itself.
    kotlin("jvm") version "1.9.23" apply false
}

// This block applies to ALL sub-modules automatically!
allprojects {
    repositories {
        mavenCentral()
    }
}