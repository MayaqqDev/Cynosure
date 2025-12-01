@file:Suppress("UNCHECKED_CAST")

package dev.mayaqq.cynosure.utils

import com.mojang.serialization.Codec
import dev.mayaqq.cynosure.utils.Either.Left
import dev.mayaqq.cynosure.utils.Either.Right
import dev.mayaqq.cynosure.utils.dfu.DFUEither
import dev.mayaqq.cynosure.utils.dfu.toCynosure
import dev.mayaqq.cynosure.utils.dfu.toDFU

public sealed interface Either<out L, out R> {

    public data class Left<out L>(override val left: L) : Either<L, Nothing> {
        override val right: Nothing?
            get() = null
    }

    public data class Right<out R>(override val right: R) : Either<Nothing, R> {
        override val left: Nothing?
            get() = null
    }

    public val left: L?

    public val right: R?

    public fun swap(): Either<R, L> = when(this) {
        is Left -> Right(left)
        is Right -> Left(right)
    }

    public companion object {
        public fun <L, R> codec(left: Codec<L>, right: Codec<R>): Codec<Either<L, R>> = Codec.either(left, right)
            .xmap(DFUEither<L, R>::toCynosure, Either<L, R>::toDFU)
    }
}

public inline val Either<*, *>.isLeft: Boolean
    get() = this is Left

public inline val Either<*, *>.isRight: Boolean
    get() = this is Right

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

public inline fun <L, R, T> Either<L, R>.mapLeft(transform: (L) -> T): Either<T, R> = when (this) {
    is Left -> Left(transform(this.left))
    else -> this as Right<R>
}

public inline fun <L, R, T> Either<L, R>.mapRight(transform: (R) -> T): Either<L, T> = when (this) {
    is Right -> Right(transform(this.right))
    else -> this as Left<L>
}

public inline fun <L, T, R> Either<L, R>.fold(leftTransform: (L) -> T, rightTransform: (R) -> T): T = when(this) {
    is Left -> leftTransform(left)
    is Right -> rightTransform(right)
}

public inline fun <L, R> Either<L, R>.foldToLeft(transform: (R) -> L): L = when(this) {
    is Left -> left
    is Right -> transform(right)
}

public inline fun <L, R> Either<L, R>.foldToRight(transform: (L) -> R): R = when(this) {
    is Left -> transform(left)
    is Right -> right
}
