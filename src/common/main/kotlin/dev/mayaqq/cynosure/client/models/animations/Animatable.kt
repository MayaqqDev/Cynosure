package dev.mayaqq.cynosure.client.models.animations

import org.joml.Vector3fc

public interface Animatable {
    public fun offsetPosition(offset: Vector3fc)

    public fun offsetRotation(offset: Vector3fc)

    public fun offsetScale(offset: Vector3fc)

    public fun reset()

    public interface Provider {
        public fun getAny(key: String): Animatable?
    }
}