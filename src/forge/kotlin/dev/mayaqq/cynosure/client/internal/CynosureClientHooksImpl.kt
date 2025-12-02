package dev.mayaqq.cynosure.client.internal

import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.MODID
import dev.mayaqq.cynosure.client.internal.CynosureClientHooksEventSubscriber.CLIENT_RELOAD_LISTENERS
import dev.mayaqq.cynosure.client.internal.CynosureClientHooksEventSubscriber.DEFERRED_CLIENT_RELOAD_LISTENERS
import dev.mayaqq.cynosure.client.internal.CynosureClientHooksEventSubscriber.resourceLoaderEventFired
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ReloadableResourceManager
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.loading.FMLEnvironment

@CynosureInternal
internal class CynosureClientHooksImpl : CynosureClientHooks {
    override fun registerReloadListener(id: ResourceLocation, listener: PreparableReloadListener) {
        require(!CLIENT_RELOAD_LISTENERS.containsKey(id)) { "Attempting to register duplicate listener id $id" }
        require(CLIENT_RELOAD_LISTENERS[id] != listener) { "Attempting to register the same listener twice" }
        CLIENT_RELOAD_LISTENERS[id] = listener

        if (FMLEnvironment.dist == Dist.CLIENT) {
            if (resourceLoaderEventFired) {
                (Minecraft.getInstance().resourceManager as ReloadableResourceManager).registerReloadListener(listener)
            } else {
                DEFERRED_CLIENT_RELOAD_LISTENERS.add(listener)
            }
        }
    }
}

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public object CynosureClientHooksEventSubscriber {
    internal val CLIENT_RELOAD_LISTENERS: MutableMap<ResourceLocation, PreparableReloadListener> = mutableMapOf()
    internal val DEFERRED_CLIENT_RELOAD_LISTENERS: MutableList<PreparableReloadListener> = mutableListOf()
    internal var resourceLoaderEventFired = false

    @SubscribeEvent
    public fun onRegisterReloadListeners(event: RegisterClientReloadListenersEvent) {
        DEFERRED_CLIENT_RELOAD_LISTENERS.forEach { event.registerReloadListener(it) }
        DEFERRED_CLIENT_RELOAD_LISTENERS.clear()
        resourceLoaderEventFired = true
    }
}