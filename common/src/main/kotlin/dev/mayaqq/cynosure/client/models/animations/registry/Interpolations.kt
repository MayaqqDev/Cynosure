package dev.mayaqq.cynosure.client.models.animations.registry

import dev.mayaqq.cynosure.client.models.animations.Keyframe
import dev.mayaqq.cynosure.core.registry.NamedRegistry
import dev.mayaqq.cynosure.modId
import net.minecraft.util.Mth
import org.joml.Vector3f
import kotlin.math.max

public object Interpolations {

    public val REGISTRY: NamedRegistry<Keyframe.Interpolation> = NamedRegistry()
    
    public val LINEAR: Keyframe.Interpolation = REGISTRY.register(modId("linear"), Keyframe.Interpolation { vector, delta, keyframes, currentFrame, targetFrame, strength ->
        keyframes[currentFrame].target.lerp(keyframes[targetFrame].target, delta, vector).mul(strength)
    })

    public val CATMULLROM: Keyframe.Interpolation = REGISTRY.register(modId("catmullrom"), Keyframe.Interpolation { vector, delta, keyframes, currentFrame, targetFrame, strength ->
        val vector3f: Vector3f = keyframes[max(0.0, (currentFrame - 1).toDouble()).toInt()].target
        val vector3f2: Vector3f = keyframes[currentFrame].target
        val vector3f3: Vector3f = keyframes[targetFrame].target
        val vector3f4: Vector3f = keyframes[(keyframes.size - 1).coerceAtMost(targetFrame + 1)].target
        vector.set(
            Mth.catmullrom(delta, vector3f.x(), vector3f2.x(), vector3f3.x(), vector3f4.x()) * strength,
            Mth.catmullrom(delta, vector3f.y(), vector3f2.y(), vector3f3.y(), vector3f4.y()) * strength,
            Mth.catmullrom(delta, vector3f.z(), vector3f2.z(), vector3f3.z(), vector3f4.z()) * strength
        )
    })
}