package dev.mayaqq.cynosure.data

import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.client.internal.CynosureClientHooks
import dev.mayaqq.cynosure.internal.CynosureHooks
import dev.mayaqq.cynosure.utils.Environment
import dev.mayaqq.cynosure.utils.PlatformHooks
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener

@OptIn(CynosureInternal::class)
public fun registerResourcepackReloadListener(id: ResourceLocation, listener: PreparableReloadListener) {
    if (PlatformHooks.environment == Environment.CLIENT) CynosureClientHooks.registerReloadListener(id, listener)
}

@OptIn(CynosureInternal::class)
public fun registerDatapackReloadListener(id: ResourceLocation, listener: PreparableReloadListener) {
    CynosureHooks.registerReloadListener(id, listener)
}