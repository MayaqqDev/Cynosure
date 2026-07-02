@file:Suppress("PropertyName", "UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.modpublish)
    alias(libs.plugins.cloche)
    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.serialization") version libs.versions.kotlin
    // Need to explicitly set ksp versions cs cloche loads an old version by default
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
    //id("dev.isxander.secrets") version "0.1.0"
    `maven-publish`
}

val modVersion = providers.gradleProperty("version").get()
val mod_name: String by project
val mod_id = providers.gradleProperty("modid").get()
val author: String by project
val mod_description = providers.gradleProperty("description").get()
val mod_license = providers.gradleProperty("license").get()

repositories {
    maven(url = "https://libraries.minecraft.net/")
    mavenCentral()
    maven(url = "https://maven.msrandom.net/repository/root") { name = "Ashley" }
    maven(url = "https://maven.parchmentmc.org") { name = "Parchment" }
    maven(url = "https://maven.fabricmc.net") { name = "FabricMC" }
    maven(url = "https://maven.terraformersmc.com/releases/") { name = "TerraformersMC" }
    maven(url = "https://thedarkcolour.github.io/KotlinForForge/") { name = "KotlinForForge" }
    maven(url = "https://maven.minecraftforge.net/") { name = "Forge" }
    maven(url = "https://repo.spongepowered.org/repository/maven-public/") { name = "Sponge / Mixin" }
    maven(url = "https://maven.resourcefulbees.com/repository/maven-public/") { name = "ResourcefulBees" }
    maven(url = "https://maven.is-immensely.gay/releases")
    maven(url = "https://maven.is-immensely.gay/nightly")
    maven(url = "https://api.modrinth.com/maven")
    maven(url = "https://maven.neoforged.net/releases")
    cloche {
        mavenNeoforged()
        mavenNeoforgedMeta()
    }
    mavenLocal()
}

cloche {
    metadata {
        modId = mod_id
        name = mod_name
        description = mod_description
        license = mod_license
        icon = "icon.png"
        url = "https://github.com/MayaqqDev/Cynosure"
        sources = "https://github.com/MayaqqDev/Cynosure"
        author(author)
        contributor("serenyadev")
    }

    val root = common {
        mixins.from(file("src/common/main/cynosure.mixins.json"))
        accessWideners.from(file("src/common/main/cynosure.accesswidener"))

        dependencies {
            compileOnly(libs.mixin)
            implementation(libs.mixinextras)
            annotationProcessor(libs.mixinextras)
            implementation(libs.kotlin.metadata) { isTransitive = false }
            api(libs.kotlinx.serialization)
            api(libs.kotlinx.coroutines)
            api(libs.bytecodecs)
            api(libs.javax.annotations)
            implementation(skipIncludeTransformation(libs.nullevt)) {
                /*
                attributes {
                    attribute(fixedAttribute, true)
                }
                 */
            }
        }
    }

    val fabricCommon = common("common:fabric") {
        dependsOn(root)

        mixins.from(file("src/common/fabric/main/cynosure.fabric.mixins.json"))

        metadata {
            custom(
                "modmenu", mapOf(
                    "badges" to listOf("library"),
                    "updateChecker" to false
                )
            )

            dependency {
                modId = "fabric-api"
            }
            dependency {
                modId = "fabric-language-kotlin"
                version {
                    start = "1.13.7+kotlin.2.2.21"
                    startInclusive = true
                }
            }
        }

        dependencies {
            modApi(libs.fabric.kotlin)
            api(libs.javax.annotations)

            include(libs.kotlin.metadata)
            include(libs.bytecodecs)

            modCompileOnly("maven.modrinth:iris:1.7.6+1.20.1") { isTransitive = false }
        }
    }

    val forgeLike = common("common:forgeLike") {
        dependsOn(root)
        mixins.from(file("src/common/forgeLike/main/cynosure.forge.mixins.json"))

        dependencies {
            api(libs.javax.annotations)

            include(libs.bytecodecs)
            include(libs.kotlin.metadata)
            include(libs.kotlin.reflect)

            modCompileOnly("maven.modrinth:oculus:1.20.1-1.8.0") { isTransitive = false }
        }
    }

    val common1201 = common("common:1.20.1") {
        dependsOn(root)
        mixins.from(file("src/common/1.20.1/main/cynosure-1.20.1.mixins.json"))
    }

    val common1211 = common("common:1.21.1") {
        dependsOn(root)
        mixins.from(file("src/common/1.21.1/main/cynosure-1.21.1.mixins.json"))
        accessWideners.from(file("src/common/1.21.1/main/cynosure-1211.accesswidener"))
    }

    fabric("fabric:1.20.1") {
        dependsOn(fabricCommon, common1201)
        mappings {
            official()
            parchment(libs.versions.parchment1201)
        }
        loaderVersion = libs.versions.fabric
        minecraftVersion = libs.versions.minecraft1201

        includedClient()

        dependencies {
            fabricApi(libs.versions.fapi1201)
            modImplementation(libs.fabric.kritter1201)
            include(libs.fabric.kritter1201)
        }

        metadata {
            entrypoint("kritter:init") {
                value.set("dev.mayaqq.cynosure::init")
            }
            entrypoint("server") {
                adapter.set("kotlin")
                value.set("dev.mayaqq.cynosure.CommonCynosureFabric::lateinit")
            }
            entrypoint("client") {
                adapter.set("kotlin")
                value.set("dev.mayaqq.cynosure.CommonCynosureFabric::lateinit")
            }
        }
    }

    fabric("fabric:1.21.1") {
        mappings {
            official()
            parchment(libs.versions.parchment1211)
        }
        dependsOn(fabricCommon, common1211)
        loaderVersion = libs.versions.fabric
        minecraftVersion = libs.versions.minecraft1211

        includedClient()

        dependencies {
            fabricApi(libs.versions.fapi1211)
            modImplementation(libs.fabric.kritter1211)

            include(libs.fabric.kritter1211)
        }

        metadata {
            entrypoint("kritter:init") {
                value.set("dev.mayaqq.cynosure::init")
            }
            entrypoint("server") {
                adapter.set("kotlin")
                value.set("dev.mayaqq.cynosure.CommonCynosureFabric::lateinit")
            }
            entrypoint("client") {
                adapter.set("kotlin")
                value.set("dev.mayaqq.cynosure.CommonCynosureFabric::lateinit")
            }
        }
    }

    forge("forge:1.20.1") {
        mappings {
            official()
            parchment(libs.versions.parchment1201)
        }
        dependsOn(forgeLike, common1201)
        loaderVersion = libs.versions.forge1201
        minecraftVersion = libs.versions.minecraft1201

        mixins.from(file("src/forge/1.20.1/main/cynosure-1.20.1.neoforge.mixins.json"))

        metadata {
            modLoader = "kritter"
            loaderVersion("2.0.0")
            blurLogo = false
        }

        dependencies {

            api(libs.forge.kotlin1201)
            modImplementation(skipIncludeTransformation(libs.forge.kritter1201))
            modImplementation(libs.forge.kritter1201.lang)

            include(libs.nullevt) {
                /*
                attributes {
                    attribute(fixedAttribute, true)
                }
                 */
            }

            include(libs.kotlin.reflect)
            include(libs.forge.kritter1201) { isTransitive = false }
        }
    }

    neoforge("neoforge:1.21.1") {
        dependsOn(forgeLike, common1211)
        mappings {
            official()
            parchment(libs.versions.parchment1211)
        }
        loaderVersion = libs.versions.neoforge1211
        minecraftVersion = libs.versions.minecraft1211

        mixins.from(file("src/neoforge/1.21.1/main/cynosure-1.21.1.neoforge.mixins.json"))

        metadata {
            modLoader = "kritter"
            loaderVersion("2.0.0")
            blurLogo = false
        }

        dependencies {
            api(libs.forge.kotlin1211)
            modImplementation(skipIncludeTransformation(libs.forge.kritter1211))

            modImplementation(libs.forge.kritter1211.lang)

            include(libs.forge.kritter1211) { isTransitive = false }
        }
    }
}

java {
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_0
        optIn.addAll("dev.mayaqq.cynosure.CynosureInternal", "kotlin.contracts.ExperimentalContracts")
        freeCompilerArgs.addAll(
            "-Xjvm-default=all-compatibility",
            "-Xcontext-receivers",
            "-Xmulti-platform",
            "-Xno-check-actual",
            "-Xexpect-actual-classes",
            "-XXLanguage:+ExpectRefinement"
        )
    }
    jvmToolchain(21)
    explicitApiWarning()
}

//Lemme just disable compiling java to fix issues
tasks.compileJava {
    enabled = false
}
tasks.compileKotlin {
    enabled = false
}

publishing {
    publications {
        create<MavenPublication>("mod") {
            from(components["java"])
        }
    }

    repositories {
        val username = properties["maven_username"]?.toString()
        val password = properties["maven_password"]?.toString()
        if (username != null && password != null) {
            maven("https://maven.is-immensely.gay/${properties["maven_category"]}") {
                name = "sapphoCompany"
                credentials {
                    this.username = username as String?
                    this.password = password
                }
            }
        } else {
            println("Sappho Company credentials not present.")
        }
    }
}

class PublishMetadata(val loaderName: String, val modloaders: Array<String>, val requires: Array<String>, val jar: Provider<RegularFile>, val suffix: String)