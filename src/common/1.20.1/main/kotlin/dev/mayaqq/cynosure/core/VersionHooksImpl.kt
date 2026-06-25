package dev.mayaqq.cynosure.core

import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.serialization.DataResult
import dev.mayaqq.cynosure.utils.getAttribute
import invoke.kitty.kritter.utils.color.Color
import invoke.kitty.kritter.utils.color.floatAlpha
import invoke.kitty.kritter.utils.color.floatBlue
import invoke.kitty.kritter.utils.color.floatGreen
import invoke.kitty.kritter.utils.color.floatRed
import invoke.kitty.kritter.utils.result.failure
import invoke.kitty.kritter.utils.result.success
import net.minecraft.nbt.NbtAccounter
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.FastColor
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeInstance
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.level.block.Block

internal object VersionHooksImpl : VersionHooks {
    override fun makeIdentifier(namespace: String, path: String): ResourceLocation = ResourceLocation(namespace, path)

    override fun parseIdentifier(both: String): ResourceLocation = ResourceLocation(both)

    override fun getStatusEffect(instance: MobEffectInstance): MobEffect = instance.effect

    override fun <R> DataResult<R>.toKtResult(allowPartial: Boolean): Result<R> =
        get().map(
            { it.success() },
            {
                if (allowPartial) {
                    val opt = resultOrPartial {}
                    if (opt.isPresent) opt.get().success() else RuntimeException(it.message()).failure()
                } else RuntimeException(it.message()).failure()
            }
        )

    override fun componentFromJsonLenient(json: String): MutableComponent? = Component.Serializer.fromJsonLenient(json)

    override fun componentFromJson(json: String): MutableComponent? = Component.Serializer.fromJson(json)

    override fun componentToJson(component: Component): String = Component.Serializer.toJson(component)

    override fun getLootTableId(block: Block): ResourceLocation = block.lootTable

    override fun makeNbtAccounter(quota: Long): NbtAccounter = NbtAccounter(quota)

    override fun addVertex(consumer: VertexConsumer, x: Double, y: Double, z: Double): VertexConsumer {
        return consumer.vertex(x, y, z)
    }

    override fun addVertex(consumer: VertexConsumer, x: Float, y: Float, z: Float, color: Color, u: Float, v: Float, packedOverlay: Int, packedLight: Int, normalX: Float, normalY: Float, normalZ: Float): VertexConsumer {
        consumer.vertex(x, y, z, color.floatRed, color.floatGreen, color.floatBlue, color.floatAlpha, u, v, packedOverlay, packedLight, normalX, normalY, normalZ)
        return consumer
    }

    override fun addToAttributeBuilder(
        builder: AttributeSupplier.Builder,
        attribute: Attribute
    ): AttributeSupplier.Builder = builder.add(attribute)

    override fun getPlayerAttribute(player: ServerPlayer, attribute: Attribute): AttributeInstance? = player.getAttribute(attribute)
}