@file:Suppress("ACTUAL_WITHOUT_EXPECT")
package dev.mayaqq.cynosure.utils

import dev.mayaqq.cynosure.core.Environment
import dev.mayaqq.cynosure.core.GameInstance
import dev.mayaqq.cynosure.core.Loader
import dev.mayaqq.cynosure.core.PlatformHooks
import dev.mayaqq.cynosure.core.mod.Mod
import net.minecraft.server.MinecraftServer
import net.minecraft.server.TickTask
import net.minecraft.util.thread.BlockableEventLoop
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.util.LogicalSidedProvider
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.server.ServerLifecycleHooks
import kotlin.jvm.optionals.getOrNull

internal class PlatformHooksImpl : PlatformHooks {

    override val environment: Environment = when (FMLEnvironment.dist!!) {
        Dist.CLIENT -> Environment.CLIENT
        Dist.DEDICATED_SERVER -> Environment.SERVER
    }

    override val devEnvironment: Boolean
        get() = !FMLEnvironment.production

    override fun isModLoaded(modid: String): Boolean = ModList.get().isLoaded(modid)

    override fun getMod(modid: String): Mod? {
        return ModList.get()
            .getModContainerById(modid)
            .getOrNull()
            ?.let {
                Mod(
                    modid,
                    it.modInfo.displayName,
                    it.modInfo.description,
                    it.modInfo.version.toString()
                )
            }
    }

    override fun currentLoader(): Loader = Loader.FORGE
}

internal class GameInstanceImpl : GameInstance {
    override val currentServer: MinecraftServer?
        get() = ServerLifecycleHooks.getCurrentServer()

    override fun getEventLoop(side: Environment): BlockableEventLoop<in TickTask> {
        return LogicalSidedProvider.WORKQUEUE.get(side.toForge())
    }

    private fun Environment.toForge(): LogicalSide = when (this) {
        Environment.CLIENT -> LogicalSide.CLIENT
        Environment.SERVER -> LogicalSide.SERVER
    }
}