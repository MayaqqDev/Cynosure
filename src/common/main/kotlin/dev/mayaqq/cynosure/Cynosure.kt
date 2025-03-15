package dev.mayaqq.cynosure

import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public const val MODID: String = "cynosure"
public const val NAME: String = "Cynosure"

@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
public annotation class CynosureInternal

@Suppress(names = ["NOTHING_TO_INLINE"])
public inline fun modId(path: String): ResourceLocation = ResourceLocation(MODID, path)

public object Cynosure : Logger by LoggerFactory.getLogger(NAME) {
    public fun init() {
        info("Initializing $NAME")
    }
}