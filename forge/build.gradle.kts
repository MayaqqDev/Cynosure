import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    alias(libs.plugins.moddevgradle)
    alias(libs.plugins.ksp)
}

val modid: String by project
val mod_name: String by project

legacyForge {
    version = "1.20.1-47.3.0"

    validateAccessTransformers = true
}

base {
    archivesName = "$modid-forge"
}

mixin {
    add(sourceSets.main.get(), "$modid.refmap.json")
    add(project(":common").sourceSets.main.get(), "$modid.refmap.json")
    config("$modid.mixins.json")
    config("$modid.forge.mixins.json")
}

repositories {
    maven(url = "https://thedarkcolour.github.io/KotlinForForge/") { name = "Kotlin for forge" }
}

dependencies {
    // Kotlin
    implementation(kotlin("reflect"))
    implementation(libs.forge.kotlin)
    // Mixin
    implementation(libs.mixin)
    annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT:processor")
    // Mixin Extras
    implementation(libs.mixinextras)
    annotationProcessor(libs.mixinextras)
    implementation(libs.forge.mixinextras)
    jarJar(libs.forge.mixinextras) {
        version {
            strictly("[0.4.1,)")
            prefer("0.4.1")
        }
    }
    // Kritter
    modImplementation(libs.forge.kritter)
    jarJar(libs.forge.kritter) {
        version {
            strictly("[0.1.12+1.20.1,)")
            prefer("0.1.12+1.20.1")
        }
    }

    modCompileOnly("maven.modrinth:oculus:1.20.1-1.8.0") {
        isTransitive = false
    }

    // ASM
    compileOnly(libs.asm)
    // Javax Annotations
    api(libs.javax.annotations)
    // Bytecodecs
    api(libs.bytecodecs)
    jarJar(libs.bytecodecs) {
        version {
            strictly("[1.0.2,)")
            prefer("1.0.2")
        }
    }

    // Kotlin
    implementation(libs.kotlin.metadata) { isTransitive = false }
    jarJar(libs.kotlin.metadata) {
        isTransitive = false
        version {
            strictly("[2.0.0,)")
            prefer("2.0.0")
        }
    }
    //compileOnly(libs.autoservice)
    //ksp(libs.autoservice.ksp)
    compileOnly(projects.common)
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "MixinConfigs" to "$modid.mixins.json,$modid.forge.mixins.json"
            )
        )
    }
}


tasks {
    withType<JavaCompile> { source(project(":common").sourceSets.main.get().allSource) }

    withType<KotlinCompile> { source(project(":common").sourceSets.main.get().allSource) }

    named("sourcesJar", Jar::class) { from(project(":common").sourceSets.main.get().allSource) }

    processResources { from(project(":common").sourceSets.main.get().resources) }
}

publishing.publications {
    create<MavenPublication>("maven") {
        artifactId = base.archivesName.get()
        from(components["java"])
    }
}