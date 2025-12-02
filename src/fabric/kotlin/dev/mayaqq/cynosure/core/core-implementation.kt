package dev.mayaqq.cynosure.core

import dev.mayaqq.cynosure.core.mod.Mod
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.impl.launch.FabricLauncherBase
import net.minecraft.client.Minecraft
import net.minecraft.server.MinecraftServer
import net.minecraft.server.TickTask
import net.minecraft.util.thread.BlockableEventLoop
import java.nio.file.Path
import kotlin.jvm.optionals.getOrNull


internal class PlatformHooksImpl : PlatformHooks {

    override val environment: Environment = when (FabricLoader.getInstance().environmentType!!) {
        EnvType.CLIENT -> Environment.CLIENT
        EnvType.SERVER -> Environment.SERVER
    }

    override val gameDir: Path
        get() = FabricLoader.getInstance().gameDir

    override val devEnvironment: Boolean
        get() = FabricLoader.getInstance().isDevelopmentEnvironment

    override fun isModLoaded(modid: String): Boolean {
        return FabricLoader.getInstance().isModLoaded(modid)
    }

    override fun getMod(modid: String): Mod? {
        return FabricLoader.getInstance()
            .getModContainer(modid)
            .getOrNull()
            ?.let {
                Mod(
                    modid,
                    it.metadata.name,
                    it.metadata.description,
                    it.metadata.version.friendlyString,
                    it.metadata.getIconPath(64).getOrNull()
                )
            }
    }

    override fun currentLoader(): Loader = Loader.FABRIC
}

internal object GameInstanceImpl : GameInstance {
    override var currentServer: MinecraftServer? = null
        get() {
            if (field == null && FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
                field = Minecraft.getInstance().singleplayerServer
            }
            return field
        }
        private set

    fun onLoadServer(server: MinecraftServer) {
        currentServer = server
    }

    fun onUnloadServer() {
        currentServer = null
    }

    override fun getEventLoop(side: Environment): BlockableEventLoop<in TickTask> {
        return if (side == Environment.CLIENT) Minecraft.getInstance()
        else currentServer ?: error("Cannot get server executor before server is loaded")
    }

    override fun getClassBytes(className: String): ByteArray = FabricLauncherBase.getLauncher().getClassByteArray(className, false)
}