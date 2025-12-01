package dev.mayaqq.cynosure.client.models.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.mayaqq.cynosure.Cynosure
import dev.mayaqq.cynosure.client.models.animations.AnimationDefinition
import dev.mayaqq.cynosure.client.models.animations.animate
import dev.mayaqq.cynosure.client.models.baked.BakedModelTree
import dev.mayaqq.cynosure.client.models.baked.CustomBakedModel
import dev.mayaqq.cynosure.utils.colors.Color
import net.minecraft.client.model.EntityModel
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.Entity
import org.joml.Vector3f

private val VEC_CACHE: Vector3f = Vector3f()

public class DataBasedModel<E : Entity>(
    private val innerModel: CustomBakedModel,
    private val animations: List<Pair<(E) -> AnimationState, AnimationDefinition>>?
) : EntityModel<E>(innerModel.renderType::apply) {

    public constructor(id: ResourceLocation, vararg animations: Pair<(E) -> AnimationState, ResourceLocation>) : this(
        ModelDataLoader.loadAndBakeModel(id),
        animations.mapNotNull {
            AnimationDataLoader.getAnimation(it.second)?.let { anim -> it.first to anim }
                .also { pair -> pair ?: Cynosure.warn("Missing animation definition ${it.second} for model $id") }
        }.toList()
    )

    override fun renderToBuffer(
        poseStack: PoseStack, buffer: VertexConsumer, light: Int, overlay: Int, r: Float, g: Float, b: Float, a: Float
    ) {
        poseStack.pushPose()
        innerModel.render(buffer, poseStack, Color(r, g, b, a), light, overlay)
        poseStack.popPose()
    }

    override fun setupAnim(p0: E, p1: Float, p2: Float, p3: Float, p4: Float, p5: Float) {
        if (animations.isNullOrEmpty() || innerModel !is BakedModelTree) return
        innerModel.reset()
        animations.forEach { (stateSupplier, definition) ->
            val state = stateSupplier(p0)
            if (state.isStarted) innerModel.animate(definition, state.accumulatedTime, VEC_CACHE)
        }
    }
}