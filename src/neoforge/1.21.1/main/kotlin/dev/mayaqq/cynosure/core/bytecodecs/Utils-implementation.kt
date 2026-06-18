package dev.mayaqq.cynosure.core.bytecodecs

import dev.mayaqq.cynosure.client.CynosureNeoforgeClient
import invoke.kitty.kritter.platform.ENVIRONMENT
import invoke.kitty.kritter.platform.Side
import net.minecraft.core.RegistryAccess
import net.neoforged.neoforge.server.ServerLifecycleHooks
import java.util.Objects

actual fun getRegistryAccess(): RegistryAccess {
    return if (ENVIRONMENT == Side.CLIENT) {
        CynosureNeoforgeClient.getRegistryAccess()
    } else {
        Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer(), "Server is null")
        ServerLifecycleHooks.getCurrentServer()!!.registryAccess()
    }
}