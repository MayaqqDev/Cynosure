@file:Suppress("NOTHING_TO_INLINE")
package dev.mayaqq.cynosure.utils.vectors

import org.joml.Matrix3fc
import org.joml.Vector3f
import org.joml.Vector3fc

public fun Vector3f.grow(nx: Float, ny: Float, nz: Float): Vector3f =
    set(nx.coerceAtLeast(this.x), ny.coerceAtLeast(this.y), nz.coerceAtLeast(this.z))

public fun Vector3f.shrink(nx: Float, ny: Float, nz: Float): Vector3f =
    set(nx.coerceAtMost(this.x), ny.coerceAtMost(this.y), nz.coerceAtMost(this.z))

public inline operator fun Vector3fc.component1(): Float = x()

public inline operator fun Vector3fc.component2(): Float = y()

public inline operator fun Vector3fc.component3(): Float = z()

public inline operator fun Vector3fc.plus(other: Vector3fc): Vector3f = add(other, Vector3f())

public inline operator fun Vector3fc.plus(value: Float): Vector3f = sub(value, value, value, Vector3f())

public inline operator fun Vector3f.plusAssign(other: Vector3fc) {
    add(other)
}

public inline operator fun Vector3f.plusAssign(amount: Float) {
    add(amount, amount, amount)
}

public inline operator fun Vector3fc.minus(other: Vector3fc): Vector3f = sub(other, Vector3f())

public inline operator fun Vector3fc.minus(amount: Float): Vector3f = sub(amount, amount, amount, Vector3f())

public inline operator fun Vector3f.minusAssign(other: Vector3fc) {
    sub(other)
}

public inline operator fun Vector3f.minusAssign(amount: Float) {
    sub(amount, amount, amount)
}

public inline operator fun Vector3fc.times(other: Vector3fc): Vector3f = mul(other, Vector3f())

public inline operator fun Vector3fc.times(scalar: Float): Vector3f = mul(scalar, Vector3f())

public inline operator fun Vector3fc.times(mat: Matrix3fc): Vector3f = mul(mat, Vector3f())

public inline operator fun Vector3f.timesAssign(vec: Vector3fc) {
    mul(vec)
}

public inline operator fun Vector3f.timesAssign(scalar: Float) {
    mul(scalar)
}

public inline operator fun Vector3f.timesAssign(mat: Matrix3fc) {
    mul(mat)
}