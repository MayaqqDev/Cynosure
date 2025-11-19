package dev.mayaqq.cynosure.client.models

import dev.mayaqq.cynosure.client.models.baked.*
import dev.mayaqq.cynosure.client.utils.grow
import dev.mayaqq.cynosure.client.utils.shrink
import dev.mayaqq.cynosure.utils.getValue
import dev.mayaqq.cynosure.utils.into
import dev.mayaqq.cynosure.utils.normalized
import dev.mayaqq.cynosure.utils.radians
import it.unimi.dsi.fastutil.ints.IntArraySet
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import net.minecraft.client.renderer.FaceInfo
import net.minecraft.core.Direction
import net.minecraft.core.Direction.Axis
import net.minecraft.util.Mth
import org.joml.*

private val BAKERY: ModelBakery by ThreadLocal.withInitial(::ModelBakery)

public fun ModelData.bake(): Result<CustomBakedModel> = runCatching {
    if (groups.isEmpty()) BAKERY.bakeSimple(this) else BAKERY.bakeAnimatable(this)
}

private const val RESCALE_22_5 = 0.08239221f
private const val RESCALE_45 = 0.4142136f
private val ZERO_VEC: Vector3fc = Vector3f(0f, 0f, 0f)

private class ModelBakery {

    private val minBound = Vector3f(Float.MAX_VALUE)
    private val maxBound = Vector3f()

    // reusing the instances
    private val position = Vector4f()
    private val normal = Vector3f()
    private val modelMat = Matrix4f()
    private val normalMat = Matrix3f()

    fun bakeSimple(data: ModelData): CustomBakedModel {
        resetBounds()
        return CustomBakedModel(bakeMesh(data.elements), data.renderType, minBound, maxBound)
    }

    fun bakeAnimatable(data: ModelData): BakedModelTree {
        val ungrouped = 0..<data.elements.size into intSetOf(data.elements.size)
        val rootGroups = mutableMapOf<String, BakedModelTree>()

        resetBounds()
        data.groups.associate {
            it.name to it.compile(data, null) { index ->
                if (ungrouped.remove(index)) data.elements[index]
                else throw IllegalStateException("Element $index referenced in multiple groups")
            }
        }

        return BakedModelTree(if(ungrouped.isEmpty()) Mesh.EMPTY else bakeMesh(ungrouped.map(data.elements::get)),
            data.renderType, Vector3f(minBound), Vector3f(maxBound), ZERO_VEC, rootGroups)
    }

    private fun ModelElementGroup.compile(root: ModelData, parent: ModelElementGroup?, resolver: (Int) -> ModelElement): BakedModelTree = BakedModelTree(
        bakeMesh(indices.map(resolver)),
        renderType ?: root.renderType,
        Vector3f(minBound), Vector3f(maxBound), origin,
        subgroups.associate { (parent?.let { "${parent.name}/$name" } ?: name) to it.compile(root, parent, resolver) }
    )

    private fun resetBounds() {
        minBound.set(Float.MAX_VALUE)
        maxBound.set(0f)
    }

    private fun bakeMesh(elements: List<ModelElement>): Mesh {
        if(elements.isEmpty()) return Mesh.EMPTY
        val vertices = IntArray(elements.sumOf { it.faces.size } * 4 * STRIDE)
        var index = 0

        for(element in elements) {

            // Reset the matrices
            modelMat.identity()
            normalMat.identity()

            // Apply element rotation
            element.rotation?.applyTo(modelMat, normalMat)

            // Set up shape for this element
            val shape = setupShape(element.from, element.to)

            for((direction, face) in element.faces) {

                for(i in 0..3) {
                    val vertex = FaceInfo.fromFacing(direction).getVertexInfo(i)

                    // Set position and normal vector
                    position.set(shape[vertex.xFace], shape[vertex.yFace], shape[vertex.zFace], 1f)
                    normal.set(direction.stepX.toFloat(), direction.stepY.toFloat(), direction.stepZ.toFloat())

                    // Multiply by the matrices
                    position.mul(modelMat)
                    normal.mul(normalMat)

                    // Grow/shrink the model bounds
                    minBound.shrink(position.x, position.y, position.z)
                    maxBound.grow(position.x, position.y, position.z)

                    // Encode vertices into array
                    val pos = index * STRIDE
                    vertices[pos] = position.x.toBits()
                    vertices[pos + 1] = position.y.toBits()
                    vertices[pos + 2] = position.z.toBits()
                    vertices[pos + 3] = face.getU(i).toBits()
                    vertices[pos + 4] = face.getV(i).toBits()
                    vertices[pos + 5] = normal.packNormal()
                    index++
                }
            }
        }

        return Mesh(vertices)
    }
}

private fun setupShape(min: Vector3f, max: Vector3f): FloatArray = FloatArray(Direction.entries.size).also {
    it[FaceInfo.Constants.MIN_X] = min.x / 16.0f
    it[FaceInfo.Constants.MIN_Y] = min.y / 16.0f
    it[FaceInfo.Constants.MIN_Z] = min.z / 16.0f
    it[FaceInfo.Constants.MAX_X] = max.x / 16.0f
    it[FaceInfo.Constants.MAX_Y] = max.y / 16.0f
    it[FaceInfo.Constants.MAX_Z] = max.z / 16.0f
}

private fun intSetOf(size: Int = 0): IntSet = when {
    size in 1..4 -> IntArraySet()
    else -> IntOpenHashSet()
}

private fun ModelElementRotation.applyTo(modelMat: Matrix4f, normalMat: Matrix3f) {
    val origin = origin
    modelMat.translate(origin.x / 16f, origin.y / 16f, origin.z / 16f)

    val quat: Quaternionf = axis.rotation(angle)
    modelMat.rotate(quat)
    normalMat.rotate(quat)

    if (rescale) {
        val scale = axis.rescaleVector.mul(if (Math.abs(angle) == 22.5f) RESCALE_22_5 else RESCALE_45)
        modelMat.scale(scale.x, scale.y, scale.z)

        val nx = 1.0f / scale.x
        val ny = 1.0f / scale.y
        val nz = 1.0f / scale.z
        val i = Mth.fastInvCubeRoot(nx * ny * nz)
        normalMat.scale(nx * i, ny * i, nz * i)
    }
    modelMat.translate(-(origin.x / 16f), -(origin.y / 16f), -(origin.z / 16f))
}

private val Axis.rescaleVector: Vector3f
    get() = when (this) {
        Axis.X -> Vector3f(0f, 1f, 1f)
        Axis.Y -> Vector3f(1f, 0f, 1f)
        Axis.Z -> Vector3f(1f, 1f, 0f)
    }


private fun Axis.rotation(degrees: Float, destination: Quaternionf = Quaternionf()): Quaternionf = when(this) {
    Axis.X -> destination.rotationX(degrees.radians)
    Axis.Y -> destination.rotationY(degrees.radians)
    Axis.Z -> destination.rotationZ(degrees.radians)
}

private fun Vector3f.packNormal(): Int =
    ((x.normalized() * PACK).toInt() and 0xFF) or (((y.normalized() * PACK).toInt() and 0xFF) shl 8) or ((((z.normalized()) * PACK).toInt() and 0xFF) shl 16)