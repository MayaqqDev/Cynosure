package dev.mayaqq.cynosure.core.registry

import com.google.common.collect.HashBiMap
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import dev.mayaqq.cynosure.utils.serialization.defaults.ResourceLocationSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.resources.ResourceLocation
import java.util.*
import java.util.function.Supplier

public class NamedRegistry<T : Any>(private val defaultValue: (() -> T)? = null) {
    private val backingMap: HashBiMap<ResourceLocation, T> = HashBiMap.create()
    private var codec: Codec<T>? = null
    private var serializer: KSerializer<T>? = null

    public fun register(key: ResourceLocation, value: T): T {
        Objects.requireNonNull(key, "Key cannot be null")
        Objects.requireNonNull(value, "Value cannot be null")
        check(!backingMap.containsKey(key)) { "Key already present: $key" }
        backingMap[key] = value
        return value
    }

    public operator fun get(key: ResourceLocation): T? = backingMap[key] ?: defaultValue?.invoke()

    public fun getKey(value: T): ResourceLocation? = backingMap.inverse()[value]

    public fun codec(): Codec<T> = codec ?: ResourceLocation.CODEC.flatXmap(
        fun(rl) = failIfNull(get(rl)) { "Entry not present: $rl" },
        fun(value) = failIfNull(getKey(value)) { "Item not found in registry: $value" }
    ).also { codec = it }

    public fun serializer(): KSerializer<T> = serializer ?: this.Serializer().also { serializer = it }

    private companion object {
        private fun <R> failIfNull(item: R?, errorMessage: Supplier<String>): DataResult<R> {
            return if (item != null) DataResult.success(item) else DataResult.error(errorMessage)
        }
    }

    private inner class Serializer : KSerializer<T> {

        override val descriptor: SerialDescriptor = SerialDescriptor(
            "dev.mayaqq.cynosure.core.registry.NamedRegistry[${this@NamedRegistry}]",
            String.serializer().descriptor
        )

        override fun deserialize(decoder: Decoder): T = this@NamedRegistry[ResourceLocationSerializer.deserialize(decoder)]!!

        override fun serialize(encoder: Encoder, value: T) {
            ResourceLocationSerializer.serialize(encoder, requireNotNull(getKey(value)))
        }
    }
}
