package dev.mayaqq.cynosure.core

import dev.mayaqq.cynosure.internal.loadPlatform
import invoke.kitty.kritter.platform.Side
import net.minecraft.server.MinecraftServer
import net.minecraft.server.TickTask
import net.minecraft.util.thread.BlockableEventLoop

public interface GameInstance {

    public companion object Impl : GameInstance by loadPlatform()

    public val currentServer: MinecraftServer?

    public fun getEventLoop(side: Side): BlockableEventLoop<in TickTask>

    public fun execute(side: Side, action: Runnable) {
        getEventLoop(side).execute(action)
    }

    public fun getClassBytes(className: String): ByteArray
}

