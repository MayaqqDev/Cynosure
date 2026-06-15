package dev.mayaqq.cynosure

import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

public const val MODID: String = "cynosure"
public const val NAME: String = "Cynosure"
internal val DEBUG_DIR: File = File(".cynosure_debug")

@RequiresOptIn(level = RequiresOptIn.Level.ERROR, message = "Internal cynosure api. Do not use")
public annotation class CynosureInternal

@Suppress("NOTHING_TO_INLINE")
public inline fun modId(path: String): ResourceLocation = ResourceLocation(MODID, path)

public object Cynosure : Logger by LoggerFactory.getLogger(NAME) {
    public fun init() {
        info("Initializing $NAME")
        if (DEBUG_DIR.exists()) DEBUG_DIR.deleteRecursively()
    }
}