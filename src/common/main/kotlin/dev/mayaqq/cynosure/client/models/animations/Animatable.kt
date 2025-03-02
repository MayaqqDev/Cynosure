package dev.mayaqq.cynosure.client.models.animations

import com.google.common.collect.HashBiMap
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.resources.ResourceLocation
import org.joml.Vector3fc
import java.util.*
import java.util.function.Supplier

interface Animatable {
    fun offsetPosition(offset: Vector3fc)

    fun offsetRotation(offset: Vector3fc)

    fun offsetScale(offset: Vector3fc)

    fun reset()

    interface Provider {
        fun getAny(key: String): Animatable?
    }
}