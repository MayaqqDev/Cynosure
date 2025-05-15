package dev.mayaqq.cynosure.data

import dev.mayaqq.cynosure.client.internal.CynosureClientHooks
import dev.mayaqq.cynosure.core.Environment
import dev.mayaqq.cynosure.core.PlatformHooks
import dev.mayaqq.cynosure.internal.CynosureHooks
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener


public fun registerResourcepackReloadListener(id: ResourceLocation, listener: PreparableReloadListener) {
    if (PlatformHooks.environment == Environment.CLIENT) CynosureClientHooks.registerReloadListener(id, listener)
}


public fun registerDatapackReloadListener(id: ResourceLocation, listener: PreparableReloadListener) {
    CynosureHooks.registerReloadListener(id, listener)
}