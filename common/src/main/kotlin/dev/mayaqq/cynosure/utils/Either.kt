@file:Suppress("UNCHECKED_CAST")

package dev.mayaqq.cynosure.utils

import com.mojang.serialization.Codec
import dev.mayaqq.cynosure.utils.Either.Left
import dev.mayaqq.cynosure.utils.Either.Right

public sealed interface Either<out L, out R> {

    public val left: L?

    public val right: R?

    public fun swap(): Either<R, L> = when(this) {
        is Left -> Right(left)
        is Right -> Left(right)
    }

    public companion object;

    public data class Left<out L, out R>(override val left: L) : Either<L, R> {
        override val right: R?
            get() = null
    }

    public data class Right<out L,  out R>(override val right: R) : Either<L, R> {
        override val left: L?
            get() = null
    }
}


public inline fun <L, R> Either<L, R>.ifLeft(action: (L) -> Unit): Either<L, R> {
    if (this is Left) action(left)
    return this
}

public inline fun <L, R> Either<L, R>.ifRight(action: (R) -> Unit): Either<L, R> {
    if (this is Right) action(right)
    return this
}

public inline fun <L, R, L2, R2> Either<L, R>.map(leftTransform: (L) -> L2, rightTransform: (R) -> R2): Either<L2, R2> = when(this) {
    is Left -> Left(leftTransform(left))
    is Right -> Right(rightTransform(right))
}

public inline fun <L, R, T> Either<L, R>.mapLeft(transform: (L) -> T): Either<T, R> = when {
    this is Left -> Left(transform(this.left))
    else -> this as Either<T, R>
}

public inline fun <L, R, T> Either<L, R>.mapRight(transform: (R) -> T): Either<L, T> = when {
    this is Right -> Right(transform(this.right))
    else -> this as Either<L, T>
}

public inline fun <L, T, R> Either<L, R>.fold(leftTransform: (L) -> T, rightTransform: (R) -> T): T = when(this) {
    is Left -> leftTransform(left)
    is Right -> rightTransform(right)
}

public inline fun <L, R> Either<L, R>.foldLeft(transform: (R) -> L): L = when(this) {
    is Left -> left
    is Right -> transform(right)
}

public inline fun <L, R> Either<L, R>.foldRight(transform: (L) -> R): R = when(this) {
    is Left -> transform(left)
    is Right -> right
}

public fun <L, R> Either.Companion.codec(left: Codec<L>, right: Codec<R>): Codec<Either<L, R>> = Codec.either(left, right)
    .xmap(com.mojang.datafixers.util.Either<L, R>::toCynosure, Either<L, R>::toDFU)

public fun <L, R> com.mojang.datafixers.util.Either<L, R>.toCynosure(): Either<L, R> =
    map({ Left(it) }, { Right(it) })

public fun <L, R> Either<L, R>.toDFU(): com.mojang.datafixers.util.Either<L, R> =
    fold({ com.mojang.datafixers.util.Either.left(it) }, { com.mojang.datafixers.util.Either.right(it) })

public inline val Either<*, *>.isLeft: Boolean
    get() = this is Left

public inline val Either<*, *>.isRight: Boolean
    get() = this is Right
