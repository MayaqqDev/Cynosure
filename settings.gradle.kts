enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "cynosure"

pluginManagement {
    repositories {
        mavenLocal()

        gradlePluginPortal()


        maven(url = "https://maven.msrandom.net/repository/cloche")
        maven(url = "https://maven.msrandom.net/repository/root")
        maven(url = "https://maven.fabricmc.net/")
        maven(url = "https://maven.minecraftforge.net/")
        maven(url = "https://repo.spongepowered.org/repository/maven-public/")
        maven(url = "https://maven.architectury.dev/")
    }
}

dependencyResolutionManagement {
    versionCatalogs.create("libs") {
        from(files("libs.versions.toml"))
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}