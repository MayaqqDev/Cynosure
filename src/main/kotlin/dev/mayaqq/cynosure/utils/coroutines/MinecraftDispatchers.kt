package dev.mayaqq.cynosure.utils.coroutines

import dev.mayaqq.cynosure.core.Environment
import dev.mayaqq.cynosure.core.GameInstance
import dev.mayaqq.cynosure.core.PlatformHooks
import dev.mayaqq.cynosure.utils.constant
import dev.mayaqq.cynosure.utils.mapBacked
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.asCoroutineDispatcher
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.server.MinecraftServer
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext

public val Dispatchers.MinecraftClient: CoroutineDispatcher by lazy {
    if(PlatformHooks.environment == Environment.CLIENT)
        MinecraftCoroutineDispatcher(Minecraft.getInstance())
    else error("Tried to access minecraft client dispatcher from server environment")
}

public val Dispatchers.MinecraftServer: CoroutineDispatcher
    get() = GameInstance.currentServer?.coroutineDispatcher ?: Main

public val Dispatchers.Background: CoroutineDispatcher by constant(Util.backgroundExecutor().asCoroutineDispatcher())

public val MinecraftServer.coroutineDispatcher: CoroutineDispatcher by mapBacked(::MinecraftCoroutineDispatcher)

internal class MinecraftCoroutineDispatcher(private val executor: Executor) : CoroutineDispatcher() {
    private var mainThreadId: Long = -1

    init {
        executor.execute {
            mainThreadId = Thread.currentThread().id
        }
    }

    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return Thread.currentThread().id != mainThreadId
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        executor.execute(block)
    }
}