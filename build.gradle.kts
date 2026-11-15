// Top-level build file
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}

// Force the Gradle wrapper script to exist even without a committed binary jar.
tasks.register("printVersion") {
    println("Game2048 - Kotlin + Compose")
}
