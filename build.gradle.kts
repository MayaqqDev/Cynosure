@file:Suppress("PropertyName")

import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("earth.terrarium.cloche") version "0.8.3"
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    `maven-publish`
}

repositories {
    maven(url = "https://maven.parchmentmc.org") { name = "Parchment" }
    maven(url = "https://maven.fabricmc.net") { name = "FabricMC" }
    maven(url = "https://maven.msrandom.net/repository/root") { name = "Ashley"}
    maven(url = "https://maven.terraformersmc.com/releases/") { name = "TerraformersMC" }
    maven(url = "https://thedarkcolour.github.io/KotlinForForge/") { name = "KotlinForForge" }
    maven(url = "https://maven.minecraftforge.net/") { name = "Forge" }
    maven(url = "https://maven.resourcefulbees.com/repository/maven-public/") { name = "ResourcefulBees" }
    mavenLocal()
    mavenCentral()
}

val mc_version: String by project
val fabric_version: String by project
val forge_version: String by project
val mixin_version: String by project
val fapi_version: String by project
val flk_version: String by project
val kff_version: String by project
val parchment_version: String by project
val byte_codecs_version: String by project

cloche {
    metadata {
        modId = "cynosure"
        name = "Cynosure"
        description = "Militech's answer to Arasaka's Soulkiller."
        license = "LGPL-3.0"
        icon = "assets/cynosure/icon.png"
        url = "https://github.com/MayaqqDev/Cynosure"
        sources = "https://github.com/MayaqqDev/Cynosure"
    }

    mappings {
        official()
        parchment(parchment_version)
    }

    common {
        mixins.from(file("src/common/main/cynosure.mixins.json"))

        dependencies {
            compileOnly("org.spongepowered:mixin:$mixin_version")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
            implementation("com.teamresourceful:bytecodecs:$byte_codecs_version")

        }
    }

    fabric("fabric:$mc_version") {
        loaderVersion = fabric_version
        minecraftVersion = mc_version

        runs {
            includedClient()
            client {
                arguments("--username", "Mayaqq", "--uuid", "a1732122-e22e-4edf-883c-09673eb55de8")
            }
            server()
        }

        dependencies {

            fabricApi("$fapi_version+$mc_version")
            modApi("net.fabricmc:fabric-language-kotlin:$flk_version")
        }

        metadata {
            entrypoint("main") {
                adapter.set("kotlin")
                value.set("dev.mayaqq.cynosure.CynosureFabric::init")
            }
            entrypoint("client") {
                adapter.set("kotlin")
                value.set("dev.mayaqq.cynosure.client.CynosureClientFabric::init")
            }
        }
    }

    forge("forge:$mc_version") {
        loaderVersion = forge_version
        minecraftVersion = mc_version

        runs {
            client {
                arguments("--username", "Mayaqq", "--uuid", "a1732122-e22e-4edf-883c-09673eb55de8")
            }
            server()
        }

        dependencies {
            api("thedarkcolour:kotlinforforge:$kff_version")
        }
    }
}


java {
    withSourcesJar()
}

tasks.withType<KotlinCompile> {
    explicitApiMode = org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Warning
    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_0
        freeCompilerArgs = listOf("-Xmulti-platform", "-Xno-check-actual", "-Xexpect-actual-classes")
    }
}

publishing {
    publications {
        create<MavenPublication>("mod") {
            from(components["java"])
        }
    }

    repositories {
        val username = "sapphoCompanyUsername".let { System.getenv(it) ?: findProperty(it) }?.toString()
        val password = "sapphoCompanyPassword".let { System.getenv(it) ?: findProperty(it) }?.toString()
        if (username != null && password != null) {
            maven("https://maven.is-immensely.gay/${properties["maven_category"]}") {
                name = "sapphoCompany"
                credentials {
                    this.username = username
                    this.password = password
                }
            }
        } else {
            println("Sappho Company credentials not present.")
        }
    }
}