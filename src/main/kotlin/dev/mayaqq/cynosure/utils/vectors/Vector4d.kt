@file:Suppress("NOTHING_TO_INLINE")
package dev.mayaqq.cynosure.utils.vectors

import org.joml.Matrix4dc
import org.joml.Vector4d
import org.joml.Vector4dc

public inline operator fun Vector4dc.component1(): Double = x()

public inline operator fun Vector4dc.component2(): Double = y()

public inline operator fun Vector4dc.component3(): Double = z()

public inline operator fun Vector4dc.component4(): Double = w()

public inline operator fun Vector4dc.plus(other: Vector4dc): Vector4d = add(other, Vector4d())

public inline operator fun Vector4dc.plus(value: Double): Vector4d = sub(value, value, value, value, Vector4d())

public inline operator fun Vector4d.plusAssign(other: Vector4dc) {
    add(other)
}

public inline operator fun Vector4d.plusAssign(amount: Double) {
    add(amount, amount, amount, amount)
}

public inline operator fun Vector4dc.minus(other: Vector4dc): Vector4d = sub(other, Vector4d())

public inline operator fun Vector4dc.minus(amount: Double): Vector4d = sub(amount, amount, amount, amount, Vector4d())

public inline operator fun Vector4d.minusAssign(other: Vector4dc) {
    sub(other)
}

public inline operator fun Vector4d.minusAssign(amount: Double) {
    sub(amount, amount, amount, amount)
}

public inline operator fun Vector4dc.times(other: Vector4dc): Vector4d = mul(other, Vector4d())

public inline operator fun Vector4dc.times(scalar: Double): Vector4d = mul(scalar, Vector4d())

public inline operator fun Vector4dc.times(mat: Matrix4dc): Vector4d = mul(mat, Vector4d())

public inline operator fun Vector4d.timesAssign(vec: Vector4dc) {
    mul(vec)
}

public inline operator fun Vector4d.timesAssign(scalar: Double) {
    mul(scalar)
}

public inline operator fun Vector4d.timesAssign(mat: Matrix4dc) {
    mul(mat)
}