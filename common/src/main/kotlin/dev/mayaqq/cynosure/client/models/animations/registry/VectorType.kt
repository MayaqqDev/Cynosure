package dev.mayaqq.cynosure.client.models.animations.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.mayaqq.cynosure.core.codecs.Codecs
import dev.mayaqq.cynosure.core.codecs.fieldOf
import dev.mayaqq.cynosure.core.registry.NamedRegistry
import dev.mayaqq.cynosure.modId
import dev.mayaqq.cynosure.utils.radians
import net.minecraft.util.ExtraCodecs
import org.joml.Vector3f
import org.joml.Vector3fc

public fun interface VectorType {

    public fun transform(input: Vector3fc): Vector3f

    public data class ConfiguredVec(val type: VectorType, val value: Vector3f) {

        public fun apply(): Vector3f = type.transform(value)

        public companion object {
            public val CODEC: Codec<ConfiguredVec> = Codecs.alternatives(
                RecordCodecBuilder.create { it.group(
                    VectorTypes.REGISTRY.codec() fieldOf ConfiguredVec::type,
                    ExtraCodecs.VECTOR3F fieldOf ConfiguredVec::value
                ).apply(it, ::ConfiguredVec) },
                ExtraCodecs.VECTOR3F.xmap(fun(vec) = ConfiguredVec(VectorTypes.DEFAULT, vec), ConfiguredVec::value)
            )

            public val VEC_CODEC: Codec<Vector3f> = CODEC.xmap(
                ConfiguredVec::apply,
                fun(vec) = ConfiguredVec(VectorTypes.DEFAULT, vec)
            )
        }
    }
}

public object VectorTypes {

    public val REGISTRY: NamedRegistry<VectorType> = NamedRegistry(::DEFAULT)

    @JvmField
    public val DEFAULT: VectorType = REGISTRY.register(modId("default"), VectorType { Vector3f(it) })

    @JvmField
    public val ANGLE: VectorType = REGISTRY.register(modId("angle"), VectorType {
        Vector3f(it.x().radians, it.y().radians, it.z().radians)
    })

    @JvmField
    public val SCALE: VectorType = REGISTRY.register(modId("scale"), VectorType {
        Vector3f(it.x() + 1.0f, it.y() + 1.0f, it.z() + 1.0f)
    })
}
