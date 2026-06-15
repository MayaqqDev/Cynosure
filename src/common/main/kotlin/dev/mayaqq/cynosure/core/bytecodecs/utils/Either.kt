package dev.mayaqq.cynosure.core.bytecodecs.utils

import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.core.bytecodecs.ByteCodecs
import invoke.kitty.kritter.utils.Either
import invoke.kitty.kritter.utils.Either.Left
import invoke.kitty.kritter.utils.Either.Right
import invoke.kitty.kritter.utils.fold

public typealias ByteCodecEither<L, R> = com.teamresourceful.bytecodecs.utils.Either<L, R>

@Deprecated("Use toKritter instead")
public fun <L, R> ByteCodecEither<L, R>.toCynosure(): Either<L, R> =
    map(::Left, ::Right)

public fun <L, R> ByteCodecEither<L, R>.toKritter(): Either<L, R> =
    map(::Left, ::Right)

public fun <L, R> Either<L, R>.toByteCodecs(): ByteCodecEither<L, R> =
    fold(fun(l) = ByteCodecEither.ofLeft(l), fun(r) = ByteCodecEither.ofRight(r))

public fun <L, R> Either.Companion.byteCodec(leftCodec: ByteCodec<L>, rightCodec: ByteCodec<R>): ByteCodec<Either<L, R>> =
    ByteCodecs.either(leftCodec, rightCodec)