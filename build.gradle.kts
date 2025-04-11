@file:Suppress("PropertyName")

import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

plugins {
    java
    kotlin("jvm") version libs.versions.kotlin apply false
    kotlin("plugin.serialization") version libs.versions.kotlin apply false
   // alias(libs.plugins.blossom) apply false
    alias(libs.plugins.ksp) apply false
}

subprojects {
    repositories {
        maven(url = "https://repo.spongepowered.org/repository/maven-public/") { name = "Sponge / Mixin" }
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

    val mod_name: String by project
    val author: String by project

    apply(plugin = "idea")
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "maven-publish")

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        withSourcesJar()
    }

    configure<KotlinProjectExtension> {
        jvmToolchain(17)
        explicitApiWarning()
    }

    configure<PublishingExtension> {
        repositories {
            val username = "sapphoCompanyUsername".let { System.getenv(it) ?: findProperty(it) }?.toString()
            val password = "sapphoCompanyPassword".let { System.getenv(it) ?: findProperty(it) }?.toString()
            val maven_category: String by project

            if (username != null && password != null) {
                maven("https://maven.is-immensely.gay/$maven_category") {
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

    tasks.jar {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_$mod_name" }
        }

        manifest {
            attributes(
                "Specification-Title" to name,
                "Specification-Vendor" to author,
                "Specification-Version" to archiveVersion,
                "Implementation-Title" to project.name,
                "Implementation-Version" to archiveVersion,
                "Implementation-Vendor" to author,
                //"Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
                "Timestamp" to System.currentTimeMillis(),
                "Built-On-Java" to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})",
                "Built-On-Minecraft" to "1.20.1"
            )
        }
    }

    tasks.processResources {
        val version: String by project
        val group: String by project
        val modid: String by project
        val license: String by project
        val description: String by project

        val expandProps = mapOf(
            "version" to version,
            "group" to group, //Else we target the task's group.
            "minecraft_version" to libs.versions.minecraft,
            "forge_version" to libs.versions.forge,
            "fabric_version" to libs.versions.fapi,
            "fabric_loader_version" to libs.versions.fabric,
            "mod_name" to mod_name,
            "mod_author" to author,
            "mod_id" to modid,
            "license" to license,
            "description" to description
        )

        filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "META-INF/mods.toml", "*.mixins.json")) {
            expand(expandProps)
        }
        inputs.properties(expandProps)
    }

//    tasks.withType<KotlinCompile> {
//        compilerOptions {
//            this.freeCompilerArgs.addAll("-Xjvm-default=all-compatibility", "-Xlambdas=indy")
//        }
//    }
}