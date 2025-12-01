package dev.mayaqq.cynosure.client.internal

import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.internal.CynosureHooksImpl
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener

@CynosureInternal
internal object CynosureClientHooksImpl : CynosureClientHooks {
    override fun registerReloadListener(id: ResourceLocation, listener: PreparableReloadListener) {
        CynosureHooksImpl.registerReloadListener(id, PackType.CLIENT_RESOURCES, listener)
    }
}