package dev.mayaqq.cynosure.client.models

import com.mojang.serialization.Codec
import com.mojang.serialization.Keyable
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.mayaqq.cynosure.client.models.baked.ModelRenderType
import dev.mayaqq.cynosure.utils.codecs.Either
import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.StringRepresentable.EnumCodec
import org.joml.Vector3f


data class ModelElementRotation(
    val angle: Float,
    val axis: Direction.Axis,
    val origin: Vector3f,
    val rescale: Boolean
) {
    companion object {
        val CODEC: Codec<ModelElementRotation> = RecordCodecBuilder.create { it.group(
            Codec.FLOAT.fieldOf("angle").forGetter(ModelElementRotation::angle),
            Direction.Axis.CODEC.fieldOf("axis").forGetter(ModelElementRotation::axis),
            ExtraCodecs.VECTOR3F.fieldOf("origin").forGetter(ModelElementRotation::origin),
            Codec.BOOL.optionalFieldOf("rescale", false).forGetter(ModelElementRotation::rescale)
        ).apply(it, ::ModelElementRotation) }
    }
}

data class ModelElementFace(
    val uv: FloatArray,
    val rotation: Float,
    val texture: String,
) {

    companion object {
        val CODEC: Codec<ModelElementFace> = RecordCodecBuilder.create { it.group(
            Codec.FLOAT.listOf().xmap(fun(list) = floatArrayOf(list[0], list[1], list[2], list[3]), fun(array) = listOf(*array)).fieldOf("uvs").forGetter(
                ModelElementFace::uv),
            Codec.FLOAT.optionalFieldOf("rotation", 0.0f).forGetter(ModelElementFace::rotation),
            Codec.STRING.fieldOf("texture").forGetter(ModelElementFace::texture)
        ).apply(it, ::ModelElementFace) }
    }

    private fun getShiftedIndex(index: Int): Int = ((index + rotation / 90) % 4).toInt()

    fun getU(index: Int): Float {
        val i: Int = getShiftedIndex(index)
        return this.uv[if (i != 0 && i != 1) 2 else 0] / 16f
    }

    fun getV(index: Int): Float {
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

data class ModelElement(
    val from: Vector3f,
    val to: Vector3f,
    val faces: Map<Direction, ModelElementFace>,
    val rotation: ModelElementRotation? = null,
    val shade: Boolean = true
) {
    companion object {
        val CODEC: Codec<ModelElement> = RecordCodecBuilder.create { it.group(
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

data class ModelElementGroup(
    val name: String,
    val renderType: ModelRenderType? = null,
    val origin: Vector3f,
    val indices: IntList,
    val subgroups: List<ModelElementGroup>
) {
    companion object {
        val CODEC: Codec<ModelElementGroup> = RecordCodecBuilder.create { it.group(
            Codec.STRING.fieldOf("name").forGetter(ModelElementGroup::name),
            ModelRenderType.CODEC.optionalFieldOf("renderType", null).forGetter(ModelElementGroup::renderType),
            ExtraCodecs.VECTOR3F.fieldOf("origin").forGetter(ModelElementGroup::origin),
            Codec.either(Codec.INT)
        ) }

        private fun groupFromEitherList() {

        }

        private val ModelElementGroup.indicesAndSubgroubs: List<Either<Int, ModelElementGroup>>
            get() = TODO()
    }

}


data class ModelData(
    val textures: Map<String, ResourceLocation>,
    val renderType: ModelRenderType = ModelRenderType.CUTOUT,
    val elements: List<ModelElement>,
    val groups: List<ModelElementGroup>,
) {
    companion object {

    }
}

