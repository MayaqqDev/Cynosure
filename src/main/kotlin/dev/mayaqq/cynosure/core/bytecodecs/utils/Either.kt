package dev.mayaqq.cynosure.core.bytecodecs.utils

import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.core.bytecodecs.ByteCodecs
import dev.mayaqq.cynosure.utils.Either
import dev.mayaqq.cynosure.utils.Either.Left
import dev.mayaqq.cynosure.utils.Either.Right
import dev.mayaqq.cynosure.utils.fold

public typealias ByteCodecEither<L, R> = com.teamresourceful.bytecodecs.utils.Either<L, R>

public fun <L, R> ByteCodecEither<L, R>.toCynosure(): Either<L, R> =
    map(::Left, ::Right)

public fun <L, R> Either<L, R>.toByteCodecs(): ByteCodecEither<L, R> =
    fold(fun(l) = ByteCodecEither.ofLeft(l), fun(r) = ByteCodecEither.ofRight(r))

public fun <L, R> Either.Companion.byteCodec(leftCodec: ByteCodec<L>, rightCodec: ByteCodec<R>): ByteCodec<Either<L, R>> =
    ByteCodecs.either(leftCodec, rightCodec)