// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0-Beta5" apply false
    id("org.jetbrains.kotlin.multiplatform") version "2.0.0-Beta5" apply false
    kotlin("plugin.serialization") version "2.0.0-Beta5" apply false
}

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath(libs.atomicfu.gradle.plugin)
    }
}

apply(plugin = "kotlinx-atomicfu")