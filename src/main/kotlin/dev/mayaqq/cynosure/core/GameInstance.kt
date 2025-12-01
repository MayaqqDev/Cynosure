package dev.mayaqq.cynosure.core

import dev.mayaqq.cynosure.internal.loadPlatform
import net.minecraft.server.MinecraftServer
import net.minecraft.server.TickTask
import net.minecraft.util.thread.BlockableEventLoop

public interface GameInstance {

    public companion object Impl : GameInstance by loadPlatform()

    public val currentServer: MinecraftServer?

    public fun getEventLoop(side: Environment): BlockableEventLoop<in TickTask>

    public fun execute(side: Environment, action: Runnable) {
        getEventLoop(side).execute(action)
    }

    public fun getClassBytes(className: String): ByteArray
}

