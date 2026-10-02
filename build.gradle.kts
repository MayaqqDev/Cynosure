@file:Suppress("PropertyName", "UnstableApiUsage")

plugins {
    alias(libs.plugins.cloche)
    // Kotlin
    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.serialization") version libs.versions.kotlin

    // Publishing
    alias(libs.plugins.modpublish)
    `maven-publish`
}

repositories {
    cloche {
        librariesMinecraft()
        mavenNeoforged()
        mavenForge()
        mavenNeoforgedMeta()
        mavenParchment()
    }

}

