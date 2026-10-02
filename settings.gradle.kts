enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "cynosure"
pluginManagement {
    repositories {
        mavenLocal()

        gradlePluginPortal()

        maven(url = "https://maven.msrandom.net/repository/cloche")
        maven(url = "https://maven.msrandom.net/repository/root")
    }
}

dependencyResolutionManagement {
    versionCatalogs.create("libs") {
        from(files("libs.versions.toml"))
    }
}