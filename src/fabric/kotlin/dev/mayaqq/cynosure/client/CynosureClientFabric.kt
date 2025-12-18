package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.CynosureFabric
import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.client.events.ClientReloadListenerEvent
import dev.mayaqq.cynosure.client.events.CynosureWorldRenderEventHandler
import dev.mayaqq.cynosure.client.events.KeybindRegistrationEvent
import dev.mayaqq.cynosure.client.events.ParticleFactoryRegistrationEvent
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.internal.CynosureHooksImpl
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.server.packs.PackType


public object CynosureClientFabric {
    @CynosureInternal
    public fun init() {
        CynosureFabric.lateinit()
        CynosureWorldRenderEventHandler.init()
        ClientFapiFeed.feed()

        ParticleFactoryRegistrationEvent(object : ParticleFactoryRegistrationEvent.Context {
            override fun <T : ParticleOptions> register(
                type: ParticleType<T>, factoryProvider: (SpriteSet) -> ParticleProvider<T>
            ) {
                ParticleFactoryRegistry.getInstance().register(type, factoryProvider)
            }

            override fun <T : ParticleOptions> register(type: ParticleType<T>, provider: ParticleProvider<T>) {
                ParticleFactoryRegistry.getInstance().register(type, provider)
            }
        }).post()

        KeybindRegistrationEvent(fun(mapping) { KeyBindingHelper.registerKeyBinding(mapping) }).post()
        ClientReloadListenerEvent(fun(id, listener) =
            CynosureHooksImpl.registerReloadListener(id, PackType.CLIENT_RESOURCES, listener)
        ).post()

        CynosureClient.init()
    }
}