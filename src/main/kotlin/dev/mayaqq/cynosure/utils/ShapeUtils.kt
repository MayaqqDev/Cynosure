package dev.mayaqq.cynosure.utils

import net.minecraft.core.Direction
import net.minecraft.core.Direction.Axis
import net.minecraft.core.Direction.AxisDirection
import net.minecraft.util.Mth
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.apache.commons.lang3.mutable.MutableObject
import java.util.*
import kotlin.math.max
import kotlin.math.min

public fun VoxelShape.allAxis(initial: Axis = Axis.Y): Map<Axis, VoxelShape> {
    val map: MutableMap<Axis, VoxelShape> = EnumMap(
        Axis::class.java
    )
    val initialDir = Direction.fromAxisAndDirection(initial, AxisDirection.POSITIVE)
    for (axis in Axis.entries) {
        val dir = getDirectionRotationVec(Direction.fromAxisAndDirection(axis, AxisDirection.POSITIVE).shiftToUp(initialDir))
        map[axis] = rotatedCopy(dir)
    }
    return map
}

public fun VoxelShape.allDirections(initial: Direction = Direction.UP): Map<Direction, VoxelShape> {
    val map: MutableMap<Direction, VoxelShape> = EnumMap(Direction::class.java)
    for (dir in Direction.entries) {
        map[dir] = rotatedCopy(getDirectionRotationVec(dir.shiftToUp(initial)))
    }
    return map
}

public fun VoxelShape.allHorizontalDirections(initial: Direction = Direction.SOUTH): Map<Direction, VoxelShape> {
    val map: MutableMap<Direction, VoxelShape> = EnumMap(
        Direction::class.java
    )
    for (dir in Direction.entries) {
        map[dir] = rotatedCopy(getHorizontalDirectionRotationVec(dir.shiftHorizontalToSouth(initial)))
    }
    return map
}

public fun VoxelShape.rotatedCopy(rotation: Vec3): VoxelShape {
    if (rotation == Vec3.ZERO) return this

    val result = MutableObject(Shapes.empty())
    val center = Vec3(8.0, 8.0, 8.0)

    this.forAllBoxes { x1, y1, z1, x2, y2, z2 ->
        var v1 = Vec3(x1, y1, z1).scale(16.0)
            .subtract(center)
        var v2 = Vec3(x2, y2, z2).scale(16.0)
            .subtract(center)

        v1 = v1.rotated(rotation.x.toFloat().toDouble(), Axis.X)
        v1 = v1.rotated(rotation.y.toFloat().toDouble(), Axis.Y)
        v1 = v1.rotated(rotation.z.toFloat().toDouble(), Axis.Z)
            .add(center)

        v2 = v2.rotated(rotation.x.toFloat().toDouble(), Axis.X)
        v2 = v2.rotated(rotation.y.toFloat().toDouble(), Axis.Y)
        v2 = v2.rotated(rotation.z.toFloat().toDouble(), Axis.Z)
            .add(center)

        val rotated = Block.box(
            min(v1.x, v2.x),
            min(v1.y, v2.y),
            min(v1.z, v2.z),
            max(v1.x, v2.x),
            max(v1.y, v2.y),
            max(v1.z, v2.z)
        )
        result.setValue(Shapes.or(result.value, rotated))
    }

    return result.value
}

public fun Vec3.rotated(deg: Double, axis: Axis): Vec3 {
    if (deg == 0.0) return this
    if (this === Vec3.ZERO) return this

    val angle = (deg / 180f * Math.PI).toFloat()
    val sin = Mth.sin(angle).toDouble()
    val cos = Mth.cos(angle).toDouble()
    val x = this.x
    val y = this.y
    val z = this.z

    return when (axis) {
        Axis.X -> Vec3(x, y * cos - z * sin, z * cos + y * sin)
        Axis.Y -> Vec3(x * cos + z * sin, y, z * cos - x * sin)
        Axis.Z -> Vec3(x * cos - y * sin, y * cos + x * sin, z)
    }
}

public fun VoxelShape.length(axis: Axis): Double {
    return if (isEmpty) 0.0 else max(axis) - min(axis)
}

private fun Direction.shiftToUp(initialDirection: Direction): Direction = when(initialDirection) {
    Direction.UP -> this
    Direction.DOWN -> this.opposite
    else -> {
        val rotateAxis = if (initialDirection.axis == Axis.Z) Axis.X else Axis.Z
        if(this.axisDirection == AxisDirection.POSITIVE) getClockWise(rotateAxis) else getCounterClockWise(rotateAxis)
    }
}

private fun Direction.shiftHorizontalToSouth(initial: Direction): Direction = when(initial) {
    Direction.SOUTH -> this
    Direction.NORTH -> this.opposite
    Direction.WEST -> this.clockWise
    Direction.EAST -> this.counterClockWise
    else -> error("Not a horizontal direction")
}


private fun getDirectionRotationVec(direction: Direction): Vec3 {
    return if (direction == Direction.UP) Vec3.ZERO else Vec3(
        (if (Direction.Plane.VERTICAL.test(direction)) 180 else 90).toDouble(),
        -((max(direction.get2DDataValue().toDouble(), 0.0).toInt() and 3) * 90f).toDouble(),
        0.0
    )
}

private fun getHorizontalDirectionRotationVec(direction: Direction): Vec3 {
    return if (direction == Direction.SOUTH) Vec3.ZERO else Vec3(
        0.0,
        -((max(direction.get2DDataValue().toDouble(), 0.0).toInt() and 3) * 90f).toDouble(),
        0.0
    )
}