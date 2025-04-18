package dev.mayaqq.cynosure.client.models.animations

import kotlinx.serialization.Serializable
import net.minecraft.util.StringRepresentable
import org.joml.Vector3f
import org.joml.Vector3fc

@Serializable
public data class Animation(val duration: Float, val target: Target) {

    public enum class Target(private val serialName: String) : StringRepresentable {
        POSITION("position") {
            override fun apply(animatable: Animatable, value: Vector3fc) = animatable.offsetPosition(value)
        },
        ROTATION("rotation") {
            override fun apply(animatable: Animatable, value: Vector3fc) = animatable.offsetRotation(value)
        },
        SCALE("scale") {
            override fun apply(animatable: Animatable, value: Vector3fc) = animatable.offsetScale(value)
        };

        public abstract fun apply(animatable: Animatable, value: Vector3fc)

        override fun getSerializedName(): String = serialName
    }
}

public data class Keyframe(val timestamp: Float, val target: Vector3f, val interpolation: Interpolation) {
    public fun interface Interpolation {
        public fun apply(vector: Vector3f, delta: Float, keyframes: Array<Keyframe>, currentFrame: Int, targetFrame: Int, strength: Float): Vector3f
    }
}

public data class AnimationDefinition(val repeats: Boolean, val animations: Map<String, List<Animation>>)

public fun Animatable.Provider.animate(definition: AnimationDefinition, vecCache: Vector3f) {
    TODO()
}