package dev.mayaqq.cynosure.core

import com.mojang.serialization.DataResult
import dev.mayaqq.cynosure.internal.loadPlatform
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance

public fun identifier(namespace: String, path: String): ResourceLocation = VersionHooks.makeIdentifier(namespace, path)
public fun identifier(parsable: String): ResourceLocation = VersionHooks.parseIdentifier(parsable)



public interface VersionHooks {
    public companion object Impl : VersionHooks by loadPlatform()

    public fun makeIdentifier(namespace: String, path: String): ResourceLocation
    public fun parseIdentifier(both: String): ResourceLocation

    public fun getStatusEffect(instance: MobEffectInstance): MobEffect

    public fun <R> DataResult<R>.toKtResult(allowPartial: Boolean = false): Result<R>

    public fun componentFromJsonLenient(json: String): MutableComponent?
}