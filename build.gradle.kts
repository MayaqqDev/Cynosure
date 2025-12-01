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
    `maven-publish`
}

val modVersion = providers.gradleProperty("version").get()
val mod_name: String by project
val mod_id = providers.gradleProperty("modid").get()
val author: String by project
val mod_description = providers.gradleProperty("description").get()
val mod_license = providers.gradleProperty("license").get()

repositories {
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

    mappings {
        official()
        parchment(libs.versions.parchment)
    }

    common {
        mixins.from(file("src/main/cynosure.mixins.json"))
        accessWideners.from(file("src/main/cynosure.accessWidener"))

        dependencies {
            compileOnly(libs.mixin)
            implementation(libs.mixinextras)
            annotationProcessor(libs.mixinextras)
            implementation(libs.kotlin.metadata) { isTransitive = false }
            api(libs.kotlinx.serialization)
            api(libs.kotlinx.coroutines)
            api(libs.bytecodecs)
            api(libs.javax.annotations)
            modCompileOnly(libs.kritter)
        }
    }

    fabric {
        loaderVersion = libs.versions.fabric
        minecraftVersion = libs.versions.minecraft

        mixins.from(file("src/main/cynosure.mixins.json"), file("src/fabric/cynosure.fabric.mixins.json"))
        accessWideners.from(file("src/main/cynosure.accessWidener"))

        includedClient()

        metadata {
            custom("modmenu", mapOf(
                "badges" to listOf("library"),
                "updateChecker" to false
            ))

            dependency {
                modId = "fabric-api"
            }
            dependency {
                modId = "fabric-language-kotlin"
            }

            entrypoint("preLaunch") {
                adapter.set("kotlin")
                value.set("dev.mayaqq.cynosure.CynosureFabricPreLaunchKt::onPreLaunch")
            }
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

        dependencies {
            fabricApi(libs.versions.fapi)
            modApi(libs.fabric.kotlin)
            modImplementation(libs.fabric.kritter)
            api(libs.javax.annotations)

            include(libs.kotlin.metadata)
            include(libs.bytecodecs)
            include(libs.fabric.kritter)

            modCompileOnly("maven.modrinth:iris:1.7.6+1.20.1") { isTransitive = false }
        }
    }

    forge {
        loaderVersion = libs.versions.forge
        minecraftVersion = libs.versions.minecraft

        mixins.from(file("src/main/cynosure.mixins.json"), file("src/fabric/cynosure.forge.mixins.json"))
        accessWideners.from(file("src/main/cynosure.accessWidener"))

        metadata {
            modLoader = "javafml"
            loaderVersion("47")
            blurLogo = false
        }

        dependencies {
            api(libs.forge.kotlin)
            modImplementation(libs.forge.kritter)
            api(libs.javax.annotations)

            include(libs.forge.mixinextras) { isTransitive = false }
            include(libs.forge.kritter) { isTransitive = false }
            include(libs.bytecodecs)
            include(libs.kotlin.metadata)

            modCompileOnly("maven.modrinth:oculus:1.20.1-1.8.0") { isTransitive = false }
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

tasks.named("createCommonApiStub", GenerateStubApi::class) {
    excludes.add(libs.kritter.get().group)
}

/*
publishMods {
    val nameFabric = "Cynosure $modVersion Fabric"
    val nameForge = "Cynosure $modVersion Forge"
    changelog = file("CHANGELOG.md").readText().replace("@VERSION@", modVersion)
    type = BETA

    val optionsCurseforge = curseforgeOptions {
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.add("1.20.1")
        projectId = "1259952"
        javaVersions.add(JavaVersion.VERSION_17)
        clientRequired = true
        serverRequired = true
    }

    val optionsModrinth = modrinthOptions {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = "4JVfdODB"
        minecraftVersions.add("1.20.1")
    }

    curseforge("curseforgeFabric") {
        from(optionsCurseforge)
        modLoaders.add("fabric")
        modLoaders.add("quilt")
        file(project(":fabric"))
        displayName = nameFabric
        version = "$modVersion-fabric"
        requires("fabric-api", "fabric-language-kotlin")
    }

    curseforge("curseforgeForge") {
        from(optionsCurseforge)
        modLoaders.add("forge")
        file(project(":forge"))
        displayName = nameForge
        version = "$modVersion-forge"
        requires("kotlin-for-forge")
    }

    modrinth("modrinthFabric") {
        from(optionsModrinth)
        modLoaders.add("fabric")
        modLoaders.add("quilt")
        file(project(":fabric"))
        displayName = nameFabric
        version = "$modVersion-fabric"
        requires("fabric-api", "fabric-language-kotlin")
    }

    modrinth("modrinthForge") {
        from(optionsModrinth)
        modLoaders.add("forge")
        file(project(":forge"))
        displayName = nameForge
        version = "$modVersion-forge"
        requires("kotlin-for-forge")
    }
}
*/