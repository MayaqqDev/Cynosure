package dev.mayaqq.cynosure.utils
import com.google.common.collect.HashBiMap
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.resources.ResourceLocation

/**
 * Simple registry class for serialization purposes
 * @param <T>
</T> */
public class Registry<T>(private val default: T? = null) {
    private val backingMap: HashBiMap<ResourceLocation, T?> = HashBiMap.create()
    private lateinit var _codec: Codec<T>

    public val codec: Codec<T>
        // Lazily initialize the codec if its used
        get() = if (!::_codec.isInitialized) ResourceLocation.CODEC.flatXmap(
            fun(location) = failIfNull(this.get(location)) { "Registry entry not present: $location" },
            fun(o) = failIfNull(this.getKey(o)) { "No key present for object: $o" }
        ).also { _codec = it } else _codec

    public fun register(key: ResourceLocation, value: T): T {
        check(!backingMap.containsKey(key)) { "Key already present: $key" }
        backingMap[key] = value
        return value
    }

    public operator fun set(key: ResourceLocation, value: T): T = register(key, value)

    public operator fun get(key: ResourceLocation): T? = backingMap.getOrDefault(key, default)

    public fun getKey(value: T?): ResourceLocation? = backingMap.inverse()[value]
}

private inline fun <R> failIfNull(meow: R?, crossinline errorMessage: () -> String): DataResult<R> {
    return if (meow != null) DataResult.success(meow) else DataResult.error { errorMessage() }
}