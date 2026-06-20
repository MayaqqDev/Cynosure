package dev.mayaqq.cynosure.core

import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.serialization.DataResult
import dev.mayaqq.cynosure.internal.loadPlatform
import invoke.kitty.kritter.utils.color.Color
import net.minecraft.nbt.NbtAccounter
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.level.block.Block

public fun identifier(namespace: String, path: String): ResourceLocation = VersionHooks.makeIdentifier(namespace, path)
public fun identifier(parsable: String): ResourceLocation = VersionHooks.parseIdentifier(parsable)

public fun VertexConsumer.provideVertex(x: Double, y: Double, z: Double): VertexConsumer = VersionHooks.addVertex(this, x, y, z)

public fun VertexConsumer.provideVertex(x: Float, y: Float, z: Float, color: Color, u: Float, v: Float, packedOverlay: Int, packedLight: Int, normalX: Float, normalY: Float, normalZ: Float): VertexConsumer =
    VersionHooks.addVertex(this, x, y, z, color, u, v, packedOverlay, packedLight, normalX, normalY, normalZ)

public fun Block.lootTableId(): ResourceLocation = VersionHooks.getLootTableId(this)

public interface VersionHooks {
    public companion object Impl : VersionHooks by loadPlatform()

    public fun makeIdentifier(namespace: String, path: String): ResourceLocation
    public fun parseIdentifier(both: String): ResourceLocation

    public fun getStatusEffect(instance: MobEffectInstance): MobEffect

    public fun <R> DataResult<R>.toKtResult(allowPartial: Boolean = false): Result<R>

    public fun componentFromJsonLenient(json: String): MutableComponent?
    public fun componentFromJson(json: String): MutableComponent?
    public fun componentToJson(component: Component): String

    public fun makeNbtAccounter(quota: Long): NbtAccounter

    public fun getLootTableId(block: Block): ResourceLocation

    public fun addVertex(consumer: VertexConsumer, x: Double, y: Double, z: Double): VertexConsumer

    public fun addVertex(consumer: VertexConsumer, x: Float, y: Float, z: Float, color: Color, u: Float, v: Float, packedOverlay: Int, packedLight: Int, normalX: Float, normalY: Float, normalZ: Float): VertexConsumer
}