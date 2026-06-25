package dev.mayaqq.cynosure.core

import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.serialization.DataResult
import invoke.kitty.kritter.utils.color.Color
import invoke.kitty.kritter.utils.color.toUInt
import invoke.kitty.kritter.utils.result.failure
import invoke.kitty.kritter.utils.result.success
import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.NbtAccounter
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.level.block.Block

internal object VersionHooksImpl : VersionHooks {
    override fun makeIdentifier(namespace: String, path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(namespace, path)

    override fun parseIdentifier(both: String): ResourceLocation = ResourceLocation.parse(both)

    override fun getStatusEffect(instance: MobEffectInstance): MobEffect = instance.effect.value()

    override fun <R> DataResult<R>.toKtResult(allowPartial: Boolean): Result<R> =
        mapOrElse(
            { it.success() },
            {
                if (allowPartial) {
                    val opt = resultOrPartial {}
                    if (opt.isPresent) opt.get().success() else RuntimeException(it.message()).failure()
                } else RuntimeException(it.message()).failure()
            }
        )

    override fun componentFromJsonLenient(json: String): MutableComponent? = Component.Serializer.fromJsonLenient(json, RegistryAccess.EMPTY)

    override fun componentFromJson(json: String): MutableComponent? = Component.Serializer.fromJson(json, RegistryAccess.EMPTY)

    override fun componentToJson(component: Component): String = Component.Serializer.toJson(component, RegistryAccess.EMPTY)

    override fun getLootTableId(block: Block): ResourceLocation = block.lootTable.location()

    override fun makeNbtAccounter(quota: Long): NbtAccounter = NbtAccounter.create(quota)

    override fun addVertex(consumer: VertexConsumer, x: Double, y: Double, z: Double): VertexConsumer {
        return consumer.addVertex(x.toFloat(), y.toFloat(), z.toFloat())
    }

    override fun addVertex(consumer: VertexConsumer, x: Float, y: Float, z: Float, color: Color, u: Float, v: Float, packedOverlay: Int, packedLight: Int, normalX: Float, normalY: Float, normalZ: Float): VertexConsumer {
        //TODO: does the toInt include the alpha or not?
        consumer.addVertex(x, y, z,color.toInt(), u, v, packedOverlay, packedLight, normalX, normalY, normalZ)
        return consumer
    }

    override fun addToAttributeBuilder(builder: AttributeSupplier.Builder, attribute: Attribute): AttributeSupplier.Builder = builder.add(Holder.direct(attribute))
}