package dev.mayaqq.cynosure.utils.result

import dev.mayaqq.cynosure.utils.Either
import dev.mayaqq.cynosure.utils.Either.Left
import dev.mayaqq.cynosure.utils.Either.Right

public val Result.Companion.UNIT: Result<Unit> get() = success(Unit)

public fun <A> Result<A>.omit(): Result<Unit> = map { }

public fun <A> Result<Result<A>>.flatten(): Result<A> = flatMap { it }

public inline fun <A> Result<A>.always(func: (Either<A, Throwable>) -> Unit): Result<A> = also { func(toEither()) }

public fun <A> Result<A>.toEither(): Either<A, Throwable> = fold(::Left, ::Right)

public inline fun <A, reified X : Throwable> Result<A>.extract(): Result<Either<A, X>> =
    map(::Left).flatRecover { if (it is X) Right(it).success() else it.failure() }


public infix fun <A, B> Result<A>.and(b: Result<B>): Result<Pair<A, B>> =
    flatMap { it1 -> b.map { it1 to it } }

@Suppress("UNCHECKED_CAST")
public infix fun <A, B> Result<A>.or(b: Result<B>): Result<Either<A, B>> = when {
    isSuccess -> Left(getOrThrow()).success()
    b.isSuccess -> Right(b.getOrThrow()).success()
    else -> flatMap { mapError { it.addSuppressed(b.exceptionOrNull()!!); it } as Result<Nothing> }
}