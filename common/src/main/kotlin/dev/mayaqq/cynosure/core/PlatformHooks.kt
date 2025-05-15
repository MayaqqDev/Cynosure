package dev.mayaqq.cynosure.core

import dev.mayaqq.cynosure.core.mod.Mod
import dev.mayaqq.cynosure.internal.loadPlatform

public fun isModLoaded(modid: String): Boolean = PlatformHooks.isModLoaded(modid)


public fun getMod(modid: String): Mod? = PlatformHooks.getMod(modid)


public val currentLoader: Loader get() = PlatformHooks.currentLoader()

public enum class Loader {
    FABRIC,
    FORGE,
    UNKNOWN
}

public interface PlatformHooks {
    public companion object Impl : PlatformHooks by loadPlatform()

    public val environment: Environment

    public val devEnvironment: Boolean

    public fun isModLoaded(modid: String): Boolean


    public fun getMod(modid: String): Mod?


    public fun currentLoader(): Loader
}