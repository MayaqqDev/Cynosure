package dev.mayaqq.cynosure.client.internal

import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.internal.loadPlatform
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener

@CynosureInternal
public interface CynosureClientHooks {

    public companion object Impl : CynosureClientHooks by loadPlatform()

    public fun registerReloadListener(id: ResourceLocation, listener: PreparableReloadListener)
}