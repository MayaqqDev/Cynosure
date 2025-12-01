package dev.mayaqq.cynosure.utils

import net.minecraft.core.BlockPos
import net.minecraft.core.Position
import net.minecraft.core.Vec3i
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import org.joml.*
import kotlin.math.floor

public fun Vector3fc.toBlockPos(): BlockPos = BlockPos(floor(this.x()).toInt(), floor(this.y()).toInt(), floor(this.z()).toInt())
public fun Vector3dc.toBlockPos(): BlockPos = BlockPos(floor(this.x()).toInt(), floor(this.y()).toInt(), floor(this.z()).toInt())
public fun Vec3.toBlockPos(): BlockPos = BlockPos(floor(this.x()).toInt(), floor(this.y()).toInt(), floor(this.z()).toInt())
public fun Vec3i.toBlockPos(): BlockPos = BlockPos(this.x, this.y, this.z)

public fun Vec3i.toVector3f(): Vector3f = Vector3f(this.x.toFloat(), this.y.toFloat(), this.z.toFloat())
public fun Vec3i.toVector3d(): Vector3d = Vector3d(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
public fun Vec3i.toVec3(): Vec3 = Vec3(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())

public fun Vector3fc.toVector3d(): Vector3d = Vector3d(this.x().toDouble(), this.y().toDouble(), this.z().toDouble())
public fun Vector3dc.toVector3f(): Vector3f = Vector3f(this.x().toFloat(), this.y().toFloat(), this.z().toFloat())

public fun Vector4fc.toVector4d(): Vector4d = Vector4d(this.x().toDouble(), this.y().toDouble(), this.z().toDouble(), this.w().toDouble())
public fun Vector4dc.toVector4f(): Vector4f = Vector4f(x().toFloat(), y().toFloat(), z().toFloat(), w().toFloat())

public fun Vector3fc.toVec3(): Vec3 = Vec3(this.x().toDouble(), this.y().toDouble(), this.z().toDouble())
public fun Vector3dc.toVec3(): Vec3 = Vec3(this.x(), this.y(), this.z())

public fun Vec3.toVector3f(): Vector3f = Vector3f(x().toFloat(), y().toFloat(), z().toFloat())
public fun Vec3.toVector3d(): Vector3d = Vector3d(x(), y(), z())
public fun Position.toVec3(): Vec3 = Vec3(x(), y(), z())

public fun Vec2.toVector2f(): Vector2f = Vector2f(x, y)
public fun Vector2dc.toVector2f(): Vector2f = Vector2f(x().toFloat(), y().toFloat())

public fun Vector2fc.toVec2(): Vec2 = Vec2(x(), y())
public fun Vector2dc.toVec2(): Vec2 = Vec2(x().toFloat(), y().toFloat())
