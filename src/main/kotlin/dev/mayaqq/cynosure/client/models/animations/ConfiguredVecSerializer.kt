package dev.mayaqq.cynosure.client.models.animations

import dev.mayaqq.cynosure.client.models.animations.registry.VectorType
import dev.mayaqq.cynosure.client.models.animations.registry.VectorTypes
import dev.mayaqq.cynosure.utils.serialization.defaults.Vector3fSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.json.*
import net.minecraft.resources.ResourceLocation
import org.joml.Vector3f

internal object ConfiguredVecSerializer : KSerializer<Vector3f> {
    override fun deserialize(decoder: Decoder): Vector3f {
        if (decoder !is JsonDecoder) return Vector3fSerializer.deserialize(decoder)
        val element = decoder.decodeJsonElement()
        if (element is JsonObject) {
            return VectorType.ConfiguredVec(
                VectorTypes.REGISTRY[ResourceLocation(element["type"]!!.jsonPrimitive.content)]!!,
                element["value"]!!.jsonArray.let { Vector3f(
                    it[0].jsonPrimitive.float,
                    it[1].jsonPrimitive.float,
                    it[2].jsonPrimitive.float
                ) }
            ).apply()
        } else if (element is JsonArray) {
            return Vector3f(
                element[0].jsonPrimitive.float,
                element[1].jsonPrimitive.float,
                element[2].jsonPrimitive.float
            )
        } else error("Invalid vector format")
    }

    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor(VectorType.ConfiguredVec::class.qualifiedName!!, SerialKind.CONTEXTUAL)

    override fun serialize(encoder: Encoder, value: Vector3f) {
        encoder.encodeCollection(descriptor, 3) {
            encodeFloatElement(descriptor, 0, value.x)
            encodeFloatElement(descriptor, 1, value.y)
            encodeFloatElement(descriptor, 2, value.z)
        }
    }
}