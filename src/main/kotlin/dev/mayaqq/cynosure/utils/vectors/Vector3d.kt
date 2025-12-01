@file:Suppress("NOTHING_TO_INLINE")
package dev.mayaqq.cynosure.utils.vectors

import org.joml.Matrix3dc
import org.joml.Vector3d
import org.joml.Vector3dc

public fun Vector3d.grow(nx: Double, ny: Double, nz: Double): Vector3d =
    set(nx.coerceAtLeast(this.x), ny.coerceAtLeast(this.y), nz.coerceAtLeast(this.z))

public fun Vector3d.shrink(nx: Double, ny: Double, nz: Double): Vector3d =
    set(nx.coerceAtMost(this.x), ny.coerceAtMost(this.y), nz.coerceAtMost(this.z))

public inline operator fun Vector3dc.component1(): Double = x()

public inline operator fun Vector3dc.component2(): Double = y()

public inline operator fun Vector3dc.component3(): Double = z()

public inline operator fun Vector3dc.plus(other: Vector3dc): Vector3d = add(other, Vector3d())

public inline operator fun Vector3dc.plus(value: Double): Vector3d = sub(value, value, value, Vector3d())

public inline operator fun Vector3d.plusAssign(other: Vector3dc) {
    add(other)
}

public inline operator fun Vector3d.plusAssign(amount: Double) {
    add(amount, amount, amount)
}

public inline operator fun Vector3dc.minus(other: Vector3dc): Vector3d = sub(other, Vector3d())

public inline operator fun Vector3dc.minus(amount: Double): Vector3d = sub(amount, amount, amount, Vector3d())

public inline operator fun Vector3d.minusAssign(other: Vector3dc) {
    sub(other)
}

public inline operator fun Vector3d.minusAssign(amount: Double) {
    sub(amount, amount, amount)
}

public inline operator fun Vector3dc.times(other: Vector3dc): Vector3d = mul(other, Vector3d())

public inline operator fun Vector3dc.times(scalar: Double): Vector3d = mul(scalar, Vector3d())

public inline operator fun Vector3dc.times(mat: Matrix3dc): Vector3d = mul(mat, Vector3d())

public inline operator fun Vector3d.timesAssign(vec: Vector3dc) {
    mul(vec)
}

public inline operator fun Vector3d.timesAssign(scalar: Double) {
    mul(scalar)
}

public inline operator fun Vector3d.timesAssign(mat: Matrix3dc) {
    mul(mat)
}