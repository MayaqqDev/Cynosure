@file:Suppress("PropertyName", "UnstableApiUsage")

import net.msrandom.stubs.GenerateStubApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


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
            implementation(libs.nullevt)
        }
    }

    val fabricCommon = common("common:fabric") {
        dependsOn(root)

        mixins.from(file("src/common/main/cynosure.mixins.json"), file("src/common/fabric/main/cynosure.fabric.mixins.json"))
        accessWideners.from(file("src/common/main/cynosure.accesswidener"))

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
                    start = "1.13.9+kotlin.2.3.10"
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
        mixins.from(file("src/common/main/cynosure.mixins.json"), file("src/common/forgeLike/main/cynosure.forge.mixins.json"))
        accessWideners.from(file("src/common/main/cynosure.accesswidener"))

        dependencies {
            api(libs.javax.annotations)

            include(libs.forge.mixinextras) { isTransitive = false }
            include(libs.bytecodecs)
            include(libs.kotlin.metadata)

            modCompileOnly("maven.modrinth:oculus:1.20.1-1.8.0") { isTransitive = false }
        }
    }

    fabric("fabric:1.20.1") {
        dependsOn(fabricCommon)
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
            entrypoint("main") {
                adapter.set("kotlin")
                value.set("dev.mayaqq.cynosure.CynosureFabric::init")
            }
            entrypoint("client") {
                adapter.set("kotlin")
                value.set("dev.mayaqq.cynosure.client.CynosureClientFabric::init")
            }
            entrypoint("server") {
                adapter.set("kotlin")
                value.set("dev.mayaqq.cynosure.CynosureFabric::lateinit")
            }
        }
    }

    fabric("fabric:1.21.1") {
        mappings {
            official()
            parchment(libs.versions.parchment1211)
        }
        dependsOn(fabricCommon)
        loaderVersion = libs.versions.fabric
        minecraftVersion = libs.versions.minecraft1211

        includedClient()

        dependencies {
            fabricApi(libs.versions.fapi1211)
            modImplementation(libs.fabric.kritter1211)

            include(libs.fabric.kritter1211)
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
            entrypoint("server") {
                adapter.set("kotlin")
                value.set("dev.mayaqq.cynosure.CynosureFabric::lateinit")
            }
        }
    }

    forge("forge:1.20.1") {
        mappings {
            official()
            parchment(libs.versions.parchment1201)
        }
        dependsOn(forgeLike)
        loaderVersion = libs.versions.forge1201
        minecraftVersion = libs.versions.minecraft1201

        metadata {
            modLoader = "kotlinforforge"
            loaderVersion("4.12.0")
            blurLogo = false
        }

        dependencies {
            api(libs.forge.kotlin1201)
            modImplementation(skipIncludeTransformation(libs.forge.kritter1201))

            include(libs.forge.kritter1201) { isTransitive = false }
        }
    }

    neoforge("neoforge:1.21.1") {
        mappings {
            official()
            parchment(libs.versions.parchment1211)
        }
        loaderVersion = libs.versions.neoforge1211
        minecraftVersion = libs.versions.minecraft1211

        metadata {
            modLoader = "kotlinforforge"
            loaderVersion("5.10.0")
            blurLogo = false
        }

        dependencies {
            api(libs.forge.kotlin1211)
            modImplementation(skipIncludeTransformation(libs.forge.kritter1211))

            include(libs.forge.kritter1211) { isTransitive = false }
        }
    }
}


java {
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_0
        optIn.addAll("dev.mayaqq.cynosure.CynosureInternal", "kotlin.contracts.ExperimentalContracts")
        freeCompilerArgs.addAll("-Xjvm-default=all-compatibility", "-Xcontext-receivers", "-Xmulti-platform", "-Xno-check-actual", "-Xexpect-actual-classes")
    }
    jvmToolchain(17)
    explicitApiWarning()
}

tasks.withType<KotlinCompile> {
    explicitApiMode = org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Warning
    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_0
        optIn.addAll("dev.mayaqq.cynosure.CynosureInternal", "kotlin.contracts.ExperimentalContracts")
        freeCompilerArgs.addAll("-Xjvm-default=all-compatibility", "-Xcontext-receivers", "-Xmulti-platform", "-Xno-check-actual", "-Xexpect-actual-classes")
    }
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
        val username = null // try { onePassword["op://nmnrp3mc2nkriiiwwk4f7q73jm/Sappho Maven/username"] } catch (_: Exception) { null }
        val password = null // try { onePassword["op://nmnrp3mc2nkriiiwwk4f7q73jm/Sappho Maven/password"] } catch (_: Exception) { null }
//        if (username != null && password != null) {
//            maven("https://maven.is-immensely.gay/${properties["maven_category"]}") {
//                name = "sapphoCompany"
//                credentials {
//                    this.username = username.get() as String?
//                    this.password = password.get()
//                }
//            }
//        } else {
//            println("Sappho Company credentials not present.")
//        }
    }
}

class PublishMetadata(val loaderName: String, val modloaders: Array<String>, val requires: Array<String>, val jar: Provider<RegularFile>, val suffix: String)