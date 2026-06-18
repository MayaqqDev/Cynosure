package dev.mayaqq.cynosure.core.bytecodecs

import dev.mayaqq.cynosure.client.CynosureClientFabric
import dev.mayaqq.cynosure.events.api.Subscription
import dev.mayaqq.cynosure.events.server.ServerEvent
import invoke.kitty.kritter.platform.ENVIRONMENT
import invoke.kitty.kritter.platform.Side
import net.minecraft.core.RegistryAccess
import net.minecraft.server.MinecraftServer
import java.util.Objects

private var server: MinecraftServer? = null

actual fun getRegistryAccess(): RegistryAccess {
    return if (ENVIRONMENT == Side.CLIENT) {
        CynosureClientFabric.getRegistryAccess()
    } else {
        Objects.requireNonNull(server, "Server is null")
        return server!!.registryAccess()
    }
}

@Subscription
fun onServerStart(event: ServerEvent.Starting) {
    server = event.server
}