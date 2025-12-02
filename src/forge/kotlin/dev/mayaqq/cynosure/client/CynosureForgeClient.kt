package dev.mayaqq.cynosure.client

import com.mojang.blaze3d.systems.RenderSystem
import dev.mayaqq.cynosure.Cynosure
import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.MODID
import dev.mayaqq.cynosure.client.events.CoreShaderRegistrationEvent
import dev.mayaqq.cynosure.client.events.ParticleFactoryRegistrationEvent
import dev.mayaqq.cynosure.client.events.entity.RenderLayerRegistrationEvent
import dev.mayaqq.cynosure.client.render.gui.HudOverlayRegistry
import dev.mayaqq.cynosure.client.render.gui.VanillaHud
import dev.mayaqq.cynosure.events.api.post
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.model.EntityModel
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent
import net.minecraftforge.client.event.RegisterParticleProvidersEvent
import net.minecraftforge.client.event.RegisterShadersEvent
import net.minecraftforge.client.gui.overlay.ForgeGui
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
public object CynosureForgeClient {
    @SubscribeEvent
    public fun clientSetup(event: FMLClientSetupEvent) {
        CynosureClient.init()
    }

    @SubscribeEvent
    public fun registerParticles(event: RegisterParticleProvidersEvent) {
        ParticleFactoryRegistrationEvent(object : ParticleFactoryRegistrationEvent.Context {
            override fun <T : ParticleOptions> register(type: ParticleType<T>, provider: ParticleProvider<T>) {
                event.registerSpecial(type, provider)
            }

            override fun <T : ParticleOptions> register(type: ParticleType<T>, factoryProvider: (SpriteSet) -> ParticleProvider<T>) {
                event.registerSpriteSet(type, factoryProvider)
            }
        }).post(context = event)
    }

    @SubscribeEvent
    public fun registerGuiOverlays(event: RegisterGuiOverlaysEvent) {
        VanillaHud.entries.forEach {
            require(VanillaGuiOverlay.entries.find { e -> e.id() == it.forgeId } != null) { "$it has an incorrect forge id" }
            event.registerBelow(it.forgeId, "cynosure_overlays_${it.forgeId.path}") { forgeGui: ForgeGui, guiGraphics: GuiGraphics, fl: Float, i: Int, i1: Int ->
                RenderSystem.enableBlend()
                RenderSystem.disableDepthTest()
                HudOverlayRegistry.sorted[it]?.forEach { overlay -> overlay.render(forgeGui, guiGraphics, fl) }
            }
        }
    }



    @SubscribeEvent
    public fun onRegisterShaders(event: RegisterShadersEvent) {
        Cynosure.info("Firing Core Shader Registration Event")
        CoreShaderRegistrationEvent(fun(id, format, onLoad) = event.registerShader(ShaderInstance(event.resourceProvider, id, format), onLoad))
            .post(context = event) { Cynosure.error("Error registering shaders", it) }
    }

    @SubscribeEvent
    @CynosureInternal
    public fun onRegisterRenderLayers(event: EntityRenderersEvent.AddLayers) {
        RenderLayerRegistrationEvent(Minecraft.getInstance().entityRenderDispatcher, event.entityModels, object : RenderLayerRegistrationEvent.Context {
            override fun getSkin(name: String): EntityRenderer<out Player>? = event.getSkin(name)

            override fun <T : LivingEntity> getEntity(entity: EntityType<T>): LivingEntityRenderer<T, EntityModel<T>>? = event.getRenderer(entity)
        }).post(context = event) { Cynosure.error("Error registering entity layers", it) }
    }
}