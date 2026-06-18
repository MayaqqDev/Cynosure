@file:Suppress("ACTUAL_WITHOUT_EXPECT")
package dev.mayaqq.cynosure.core

import dev.mayaqq.cynosure.core.mod.Mod
import invoke.kitty.kritter.platform.Side
import net.minecraft.server.MinecraftServer
import net.minecraft.server.TickTask
import net.minecraft.util.thread.BlockableEventLoop
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.LogicalSide
import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.fml.loading.FMLPaths
import net.neoforged.neoforge.common.util.LogicalSidedProvider
import net.neoforged.neoforge.server.ServerLifecycleHooks
import java.nio.file.Path
import kotlin.jvm.optionals.getOrNull

internal class PlatformHooksImpl : PlatformHooks {

    override val environment: Side = when (FMLEnvironment.dist!!) {
        Dist.CLIENT -> Side.CLIENT
        Dist.DEDICATED_SERVER -> Side.SERVER
    }

    override val devEnvironment: Boolean
        get() = !FMLEnvironment.production

    override val gameDir: Path
        get() = FMLPaths.GAMEDIR.get()

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
                    it.modInfo.version.toString(),
                    it.modInfo.logoFile.getOrNull()
                )
            }
    }

    override fun currentLoader(): Loader = Loader.FORGE
}

internal class GameInstanceImpl : GameInstance {
    override val currentServer: MinecraftServer?
        get() = ServerLifecycleHooks.getCurrentServer()

    override fun getEventLoop(side: Side): BlockableEventLoop<in TickTask> {
        return LogicalSidedProvider.WORKQUEUE.get(side.toForge())
    }

    override fun getClassBytes(className: String): ByteArray {
        TODO("Not yet implemented")
    }

    private fun Side.toForge(): LogicalSide = when (this) {
        Side.CLIENT -> LogicalSide.CLIENT
        Side.SERVER -> LogicalSide.SERVER
    }
}