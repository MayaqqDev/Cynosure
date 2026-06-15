@file:Suppress("NOTHING_TO_INLINE")
package dev.mayaqq.cynosure.utils.vectors

import org.joml.Matrix4fc
import org.joml.Vector4f
import org.joml.Vector4fc

public inline operator fun Vector4fc.component1(): Float = x()

public inline operator fun Vector4fc.component2(): Float = y()

public inline operator fun Vector4fc.component3(): Float = z()

public inline operator fun Vector4fc.component4(): Float = w()

public inline operator fun Vector4fc.plus(other: Vector4fc): Vector4f = add(other, Vector4f())

public inline operator fun Vector4fc.plus(value: Float): Vector4f = sub(value, value, value, value, Vector4f())

public inline operator fun Vector4f.plusAssign(other: Vector4fc) {
    add(other)
}

public inline operator fun Vector4f.plusAssign(amount: Float) {
    add(amount, amount, amount, amount)
}

public inline operator fun Vector4fc.minus(other: Vector4fc): Vector4f = sub(other, Vector4f())

public inline operator fun Vector4fc.minus(amount: Float): Vector4f = sub(amount, amount, amount, amount, Vector4f())

public inline operator fun Vector4f.minusAssign(other: Vector4fc) {
    sub(other)
}

public inline operator fun Vector4f.minusAssign(amount: Float) {
    sub(amount, amount, amount, amount)
}

public inline operator fun Vector4fc.times(other: Vector4fc): Vector4f = mul(other, Vector4f())

public inline operator fun Vector4fc.times(scalar: Float): Vector4f = mul(scalar, Vector4f())

public inline operator fun Vector4fc.times(mat: Matrix4fc): Vector4f = mul(mat, Vector4f())

public inline operator fun Vector4f.timesAssign(vec: Vector4fc) {
    mul(vec)
}

public inline operator fun Vector4f.timesAssign(scalar: Float) {
    mul(scalar)
}

public inline operator fun Vector4f.timesAssign(mat: Matrix4fc) {
    mul(mat)
}