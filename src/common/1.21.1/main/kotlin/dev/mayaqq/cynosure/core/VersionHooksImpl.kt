package dev.mayaqq.cynosure.core

import com.mojang.serialization.DataResult
import invoke.kitty.kritter.utils.result.failure
import invoke.kitty.kritter.utils.result.success
import net.minecraft.core.RegistryAccess
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance

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
}