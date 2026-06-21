package dev.mayaqq.nullevtfix

import org.gradle.api.artifacts.transform.CacheableTransform
import org.gradle.api.artifacts.transform.InputArtifact
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformOutputs
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

@CacheableTransform
abstract class MakeForgeMod : TransformAction<TransformParameters.None> {

    // FMLModType: LIBRARY

    abstract val inputFile: Provider<FileSystemLocation>
        @InputArtifact
        @PathSensitive(PathSensitivity.NONE)
        get

    override fun transform(outputs: TransformOutputs) {
        val file = inputFile.get().asFile
        val outputFile = outputs.file("${file.name.substring(0, file.name.lastIndexOf('.'))}-fixed.jar")
        ZipInputStream(FileInputStream(file)).use { inputJar ->
            ZipOutputStream(FileOutputStream(outputFile)).use { outputJar ->
                var entry = inputJar.nextEntry
                while (entry != null) {
                    if (entry.name.equals("META-INF/MANIFEST.MF", ignoreCase = true)) {
                        val data = String(inputJar.readBytes())
                            .lines()
                            .toMutableList()
                            .apply { add("FmlModType: GAMELIBRARY") }
                            .joinToString("\n")

                        outputJar.putNextEntry(ZipEntry("META-INF/MANIFEST.MF"))
                        outputJar.write(data.toByteArray())
                        outputJar.closeEntry()
                        entry = inputJar.nextEntry
                    } else {
                        outputJar.putNextEntry(entry)
                        inputJar.copyTo(outputJar)
                        outputJar.closeEntry()
                        entry = inputJar.nextEntry
                    }
                }
            }
        }
    }
}