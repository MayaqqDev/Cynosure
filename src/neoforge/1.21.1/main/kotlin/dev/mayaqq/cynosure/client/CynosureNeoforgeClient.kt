package dev.mayaqq.cynosure.client

import com.mojang.blaze3d.systems.RenderSystem
import dev.mayaqq.cynosure.Cynosure
import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.MODID
import dev.mayaqq.cynosure.client.events.ClientReloadListenerEvent
import dev.mayaqq.cynosure.client.events.CoreShaderRegistrationEvent
import dev.mayaqq.cynosure.client.events.KeybindRegistrationEvent
import dev.mayaqq.cynosure.client.events.ParticleFactoryRegistrationEvent
import dev.mayaqq.cynosure.client.events.entity.RenderLayerRegistrationEvent
import dev.mayaqq.cynosure.client.render.gui.HudOverlayRegistry
import dev.mayaqq.cynosure.client.render.gui.VanillaHud
import dev.mayaqq.cynosure.client.render.vanillaGuiLayers
import dev.mayaqq.cynosure.client.utils.DefaultSkin
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.helpers.McClient
import dev.mayaqq.cynosure.modId
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.model.EntityModel
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import net.neoforged.neoforge.client.event.RegisterShadersEvent

@EventBusSubscriber(modid = MODID, value = [Dist.CLIENT])
internal object CynosureNeoforgeClient {
    @SubscribeEvent
    fun clientSetup(event: FMLClientSetupEvent) {
        CynosureForgeLikeClient.init()
    }

    @SubscribeEvent
    fun onKeybindsRegister(event: RegisterKeyMappingsEvent) {
        KeybindRegistrationEvent(fun(mapping) = event.register(mapping)).post()
    }

    @SubscribeEvent
    fun onRegisterClientListeners(event: RegisterClientReloadListenersEvent) {
        ClientReloadListenerEvent(fun(id, listener) = event.registerReloadListener(listener)).post()
    }

    @SubscribeEvent
    fun registerParticles(event: RegisterParticleProvidersEvent) {
        ParticleFactoryRegistrationEvent(object : ParticleFactoryRegistrationEvent.Context {
            override fun <T : ParticleOptions> register(type: ParticleType<T>, provider: ParticleProvider<T>) {
                event.registerSpecial(type, provider)
            }

            override fun <T : ParticleOptions> register(type: ParticleType<T>, factoryProvider: (SpriteSet) -> ParticleProvider<T>) {
                event.registerSpriteSet(type, factoryProvider)
            }
        }).post()
    }

    @SubscribeEvent
    fun registerGuiOverlays(event: RegisterGuiLayersEvent) {
        VanillaHud.ids.forEach {

            require(vanillaGuiLayers.find { e -> e == it } != null) { "$it has an incorrect forge id" }
            event.registerBelow(it, modId("overlays_${it.path}")) { guiGraphics, deltaTracker ->
                RenderSystem.enableBlend()
                RenderSystem.disableDepthTest()
                //TODO: check if using McClient.gui is okay
                HudOverlayRegistry.sorted[it]?.forEach { overlay -> overlay.render(McClient.gui, guiGraphics, deltaTracker.realtimeDeltaTicks) }
            }
        }
    }



    @SubscribeEvent
    fun onRegisterShaders(event: RegisterShadersEvent) {
        Cynosure.info("Firing Core Shader Registration Event")
        CoreShaderRegistrationEvent(fun(id, format, onLoad) = event.registerShader(ShaderInstance(event.resourceProvider, id, format), onLoad))
            .post()
    }

    @SubscribeEvent
    @CynosureInternal
    fun onRegisterRenderLayers(event: EntityRenderersEvent.AddLayers) {
        RenderLayerRegistrationEvent(Minecraft.getInstance().entityRenderDispatcher, event.entityModels, object : RenderLayerRegistrationEvent.Context {
            override fun getSkin(name: String): EntityRenderer<out Player>? = event.getSkin(PlayerSkin.Model.byName(name))

            override fun <T : LivingEntity> getEntity(entity: EntityType<T>): LivingEntityRenderer<T, EntityModel<T>>? {
                return try {
                    event.getRenderer(entity)
                } catch(_: Exception) {
                    null
                }
            }

        }).post()
    }
}