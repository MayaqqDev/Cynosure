package dev.mayaqq.cynosure.client.models.animations

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.mayaqq.cynosure.client.models.animations.registry.Interpolations
import dev.mayaqq.cynosure.client.models.animations.registry.VectorType
import dev.mayaqq.cynosure.client.models.animations.registry.VectorTypes
import dev.mayaqq.cynosure.core.codecs.Codecs
import dev.mayaqq.cynosure.core.codecs.fieldOf
import dev.mayaqq.cynosure.core.codecs.forGetter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.Mth
import org.joml.Vector3f
import org.joml.Vector3fc
import java.util.Optional
import kotlin.jvm.optionals.getOrNull
import kotlin.math.max
import kotlin.math.min

private val LOCAL_VEC_CACHE: ThreadLocal<Vector3f> = ThreadLocal.withInitial(::Vector3f)

@Serializable
public data class AnimationDefinition(val duration: Float, val looping: Boolean, val bones: Map<String, List<Animation>>) {
    public companion object {
        @JvmField
        public val CODEC: Codec<AnimationDefinition> = RecordCodecBuilder.create { it.group(
            Codec.FLOAT.fieldOf("duration").forGetter(AnimationDefinition::duration),
            Codec.BOOL.fieldOf("looping").forGetter(AnimationDefinition::looping),
            Codec.unboundedMap(Codec.STRING, Animation.CODEC.listOf())
                .fieldOf("bones").forGetter(AnimationDefinition::bones)
        ).apply(it, ::AnimationDefinition) }
    }
}

@Serializable
public data class Animation(val target: Target, val duration: Float?, val keyframes: List<Keyframe>) {

    @Serializable
    public enum class Target {
        @SerialName("position") POSITION {
            override fun apply(animatable: Animatable, value: Vector3fc) = animatable.offsetPosition(value)
        },
        @SerialName("rotation") ROTATION {
            override fun apply(animatable: Animatable, value: Vector3fc) = animatable.offsetRotation(value)
        },
        @SerialName("scale") SCALE {
            override fun apply(animatable: Animatable, value: Vector3fc) = animatable.offsetScale(value)
        };

        public abstract fun apply(animatable: Animatable, value: Vector3fc)

        public companion object {
            public val CODEC: Codec<Target> = Codec.STRING.xmap(
                fun(s) = Target.valueOf(s),
                fun(t) = Target.serializer().descriptor.getElementName(t.ordinal)
            )
        }
    }

    public companion object {
        @JvmField
        public val CODEC: Codec<Animation> = RecordCodecBuilder.create { it.group(
            Target.CODEC.fieldOf("target").forGetter(Animation::target),
            Codec.FLOAT.optionalFieldOf("duration").forGetter { Optional.ofNullable(it.duration) },
            Keyframe.CODEC.listOf().fieldOf("keyframes").forGetter(Animation::keyframes)
        ).apply(it) { target, duration, keyframes -> Animation(target, duration.getOrNull(), keyframes) } }
    }
}

@Serializable
public data class Keyframe(val timestamp: Float, val target: @Serializable(ConfiguredVecSerializer::class) Vector3f, val interpolation: Interpolation) {
    public fun interface Interpolation {
        public fun apply(vector: Vector3f, delta: Float, keyframes: List<Keyframe>, currentFrame: Int, targetFrame: Int, strength: Float): Vector3f
    }

    public companion object {
        @JvmField
        public val CODEC: Codec<Keyframe> = RecordCodecBuilder.create { it.group(
            Codec.FLOAT.fieldOf("timestamp").forGetter(Keyframe::timestamp),
            Codecs.alternatives(
                ExtraCodecs.VECTOR3F,
                VectorType.ConfiguredVec.CODEC.xmap<Vector3f>(
                    { it.apply() },
                    { VectorType.ConfiguredVec(VectorTypes.DEFAULT, it)}
                )
            ).fieldOf("target").forGetter(Keyframe::target),
            Interpolations.REGISTRY.codec() fieldOf "interpolation" forGetter Keyframe::interpolation
        ).apply(it, ::Keyframe) }
    }
}

@JvmOverloads
public fun Animatable.Provider.animate(definition: AnimationDefinition, accumulatedTime: Long, vecCache: Vector3f = LOCAL_VEC_CACHE.get()) {
    definition.bones.forEach { (key, animations) ->
        getAny(key)?.let { animatable ->
            animations.forEach { animation ->
                val keyframes: List<Keyframe> = animation.keyframes
                val elapsed: Float = definition.getElapsedSeconds(accumulatedTime, animation)
                val last = max(0.0, (Mth.binarySearch(0, keyframes.size) { index: Int -> elapsed <= keyframes[index].timestamp } - 1).toDouble()).toInt()
                val next = min((keyframes.size - 1).toDouble(), (last + 1).toDouble()).toInt()

                val lastFrame = keyframes[last]
                val nextFrame = keyframes[next]
                val sinceLast: Float = elapsed - lastFrame.timestamp
                val delta = if (next != last) Mth.clamp(sinceLast / (nextFrame.timestamp - lastFrame.timestamp), 0.0f, 1.0f) else 0.0f

                nextFrame.interpolation.apply(vecCache, delta, keyframes, last, next, 1f)
                animation.target.apply(animatable, vecCache)
            }
        }
    }
}

private fun AnimationDefinition.getElapsedSeconds(accumulatedTime: Long, animation: Animation): Float {
    val f = accumulatedTime / 1000.0
    return if (looping) f.toFloat() % (animation.duration ?: duration) else f.toFloat()
}