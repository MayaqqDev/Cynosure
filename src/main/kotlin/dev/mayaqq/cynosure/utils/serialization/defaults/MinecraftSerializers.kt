package dev.mayaqq.cynosure.utils.serialization.defaults

import dev.mayaqq.cynosure.utils.serialization.buildClassSerializer
import dev.mayaqq.cynosure.utils.serialization.fieldOf
import dev.mayaqq.cynosure.utils.serialization.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import org.joml.Vector3f
import org.joml.Vector3fc

public object ResourceLocationSerializer : KSerializer<ResourceLocation> by String.serializer().map(::ResourceLocation, ResourceLocation::toString)

public object Vector3fSerializer : KSerializer<Vector3f> by buildClassSerializer("",
    Float.serializer().fieldOf("x", Vector3fc::x),
    Float.serializer().fieldOf("y", Vector3fc::y),
    Float.serializer().fieldOf("z", Vector3fc::z),
    ::Vector3f
)

public fun <T : Any> Registry<T>.createSerializer(): RegistrySerializer<T> = RegistrySerializer.Direct(this)

public fun <T : Any> ResourceKey<out Registry<T>>.createSerializer(): RegistrySerializer<T> = RegistrySerializer.Deferred(this)

public sealed class RegistrySerializer<T : Any> : KSerializer<T> {

    protected abstract val registry: Registry<T>

    final override fun deserialize(decoder: Decoder): T {
        val id = ResourceLocationSerializer.deserialize(decoder)
        val item = registry.get(id)
        return item!!
    }

    final override val descriptor: SerialDescriptor = SerialDescriptor("dev.mayaqq.cynosure.Registry", ResourceLocationSerializer.descriptor)

    final override fun serialize(encoder: Encoder, value: T) {
        val id = registry.getKey(value)!!
        ResourceLocationSerializer.serialize(encoder, id)
    }

    internal class Direct<T: Any>(override val registry: Registry<T>) : RegistrySerializer<T>()

    internal class Deferred<T : Any>(private val key: ResourceKey<out Registry<T>>) : RegistrySerializer<T>() {
        @Suppress("UNCHECKED_CAST")
        override val registry: Registry<T>
            get() = (BuiltInRegistries.REGISTRY as Registry<Registry<T>>)
                .get(key as ResourceKey<Registry<T>>) as Registry<T>
    }
}