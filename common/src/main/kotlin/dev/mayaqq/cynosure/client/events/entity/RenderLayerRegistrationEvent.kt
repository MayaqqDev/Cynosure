package dev.mayaqq.cynosure.client.events.entity

import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.client.utils.DefaultSkin
import dev.mayaqq.cynosure.events.api.Event
import dev.mayaqq.cynosure.mixin.client.accessor.LivingEntityRendererAccessor
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.EntityModelSet
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import org.jetbrains.annotations.ApiStatus.NonExtendable


public class RenderLayerRegistrationEvent(
    public val renderers: EntityRenderDispatcher,
    public val models: EntityModelSet,
    @PublishedApi internal val context: Context
) : Event {

    public inline fun <T : LivingEntity> addLayer(entity: EntityType<T>, layer: (RenderLayerParent<T, EntityModel<T>>) -> RenderLayer<T, out EntityModel<T>>) {
        context.getEntity(entity)?.let { (it as LivingEntityRendererAccessor).invokeAddLayer(layer(it)) }
    }

    public inline fun addLayer(skin: DefaultSkin, layer: (RenderLayerParent<Player, EntityModel<Player>>) -> RenderLayer<out Player, out EntityModel<out Player>>) {
        addLayer(skin.skinName, layer)
    }

    public inline fun addLayer(skin: String, layer: (RenderLayerParent<Player, EntityModel<Player>>) -> RenderLayer<out Player, out EntityModel<out Player>>) {
        context.getSkin(skin)?.let { (it as LivingEntityRendererAccessor).invokeAddLayer(layer(it as LivingEntityRenderer<Player, EntityModel<Player>>)) }
    }

    @CynosureInternal
    @NonExtendable
    public interface Context {

        public fun getSkin(name: String): EntityRenderer<out Player>?

        public fun <T : LivingEntity> getEntity(entity: EntityType<T>): LivingEntityRenderer<T, EntityModel<T>>?
    }
}