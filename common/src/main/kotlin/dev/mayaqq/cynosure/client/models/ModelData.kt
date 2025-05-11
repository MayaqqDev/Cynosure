package dev.mayaqq.cynosure.client.models

import com.mojang.serialization.Codec
import com.mojang.serialization.Keyable
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.mayaqq.cynosure.client.models.baked.ModelRenderType
import dev.mayaqq.cynosure.utils.Either
import dev.mayaqq.cynosure.core.codecs.Codecs
import dev.mayaqq.cynosure.core.codecs.fieldOf
import net.minecraft.core.Direction
import net.minecraft.util.ExtraCodecs
import org.joml.Vector3f


public data class ModelElementRotation(
    val angle: Float,
    val axis: Direction.Axis,
    val origin: Vector3f,
    val rescale: Boolean
) {
    public companion object {
        public val CODEC: Codec<ModelElementRotation> = RecordCodecBuilder.create { it.group(
            Codec.FLOAT.fieldOf("angle").forGetter(ModelElementRotation::angle),
            Direction.Axis.CODEC.fieldOf("axis").forGetter(ModelElementRotation::axis),
            ExtraCodecs.VECTOR3F.fieldOf("origin").forGetter(ModelElementRotation::origin),
            Codec.BOOL.optionalFieldOf("rescale", false).forGetter(ModelElementRotation::rescale)
        ).apply(it, ::ModelElementRotation) }
    }
}

public data class ModelElementFace(
    val uv: FloatArray,
    val rotation: Float,
    val texture: String,
) {

    public companion object {
        public val CODEC: Codec<ModelElementFace> = RecordCodecBuilder.create { it.group(
            Codec.FLOAT.listOf().xmap(fun(list) = floatArrayOf(list[0], list[1], list[2], list[3]), fun(array) = array.toList()).fieldOf("uvs").forGetter(
                ModelElementFace::uv),
            Codec.FLOAT.optionalFieldOf("rotation", 0.0f).forGetter(ModelElementFace::rotation),
            Codec.STRING.fieldOf("texture").forGetter(ModelElementFace::texture)
        ).apply(it, ::ModelElementFace) }
    }

    private fun getShiftedIndex(index: Int): Int = ((index + rotation / 90) % 4).toInt()

    public fun getU(index: Int): Float {
        val i: Int = getShiftedIndex(index)
        return this.uv[if (i != 0 && i != 1) 2 else 0] / 16f
    }

    public fun getV(index: Int): Float {
        val i: Int = getShiftedIndex(index)
        return this.uv[if (i != 0 && i != 3) 3 else 1] / 16f
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModelElementFace

        if (rotation != other.rotation) return false
        if (texture != other.texture) return false
        if (!uv.contentEquals(other.uv)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rotation.hashCode()
        result = 31 * result + texture.hashCode()
        result = 31 * result + uv.contentHashCode()
        return result
    }
}

public data class ModelElement(
    val from: Vector3f,
    val to: Vector3f,
    val faces: Map<Direction, ModelElementFace>,
    val rotation: ModelElementRotation? = null,
    val shade: Boolean = true
) {
    public companion object {
        @JvmField
        public val CODEC: Codec<ModelElement> = RecordCodecBuilder.create { it.group(
            ExtraCodecs.VECTOR3F.fieldOf("from").forGetter(ModelElement::from),
            ExtraCodecs.VECTOR3F.fieldOf("to").forGetter(ModelElement::to),
            Codec.simpleMap(Direction.CODEC,
                ModelElementFace.CODEC, Keyable.forStrings(fun() = Direction.entries.stream().map { it.serializedName })).fieldOf("faces").forGetter(
                ModelElement::faces),
            ModelElementRotation.CODEC.optionalFieldOf("rotation", null).forGetter(ModelElement::rotation),
            Codec.BOOL.optionalFieldOf("shade", true).forGetter(ModelElement::shade)
        ).apply(it, ::ModelElement) }
    }
}

public data class ModelElementGroup(
    val name: String,
    val renderType: ModelRenderType? = null,
    val origin: Vector3f,
    val indices: List<Int>,
    val subgroups: List<ModelElementGroup>
) {
    public companion object {
        public val CODEC: Codec<ModelElementGroup> = Codecs.recursive { RecordCodecBuilder.create { it.group(
            Codec.STRING fieldOf ModelElementGroup::name,
            ModelRenderType.CODEC.optionalFieldOf("renderType", null).forGetter(ModelElementGroup::renderType),
            ExtraCodecs.VECTOR3F fieldOf ModelElementGroup::origin,
            Either.codec(Codec.INT, this).listOf().fieldOf("elements").forGetter(ModelElementGroup::indicesAndSubgroubs)
        ).apply(it, ::groupFromEitherList) } }

        private fun groupFromEitherList(name: String, renderType: ModelRenderType?, origin: Vector3f, data: List<Either<Int, ModelElementGroup>>): ModelElementGroup {
            val indices = data.mapNotNull { it.left }
            val subgroups = data.mapNotNull { it.right }
            return ModelElementGroup(name, renderType, origin, indices, subgroups)
        }
    }

}

private val ModelElementGroup.indicesAndSubgroubs: List<Either<Int, ModelElementGroup>>
    get() = indices.map { Either.Left(it) } + subgroups.map { Either.Right(it) }

public data class ModelData(
    val renderType: ModelRenderType = ModelRenderType.CUTOUT,
    val elements: List<ModelElement>,
    val groups: List<ModelElementGroup>,
) {
    public companion object {
        public val CODEC: Codec<ModelData> = RecordCodecBuilder.create { it.group(
            ModelRenderType.CODEC fieldOf ModelData::renderType,
            ModelElement.CODEC.listOf() fieldOf ModelData::elements,
            ModelElementGroup.CODEC.listOf() fieldOf ModelData::groups
        ).apply(it, ::ModelData) }
    }
}

