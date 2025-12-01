package dev.mayaqq.cynosure.utils.dfu

import dev.mayaqq.cynosure.utils.Either
import dev.mayaqq.cynosure.utils.Either.Left
import dev.mayaqq.cynosure.utils.Either.Right
import dev.mayaqq.cynosure.utils.fold

public typealias DFUPair<F, S> = com.mojang.datafixers.util.Pair<F ,S>

public typealias DFUEither<L, R> = com.mojang.datafixers.util.Either<L, R>

public fun <L, R> DFUEither<L, R>.toCynosure(): Either<L, R> = map(::Left, ::Right)

public fun <L, R> Either<L, R>.toDFU(): com.mojang.datafixers.util.Either<L, R> =
    fold({ DFUEither.left(it) }, { DFUEither.right(it) })

public fun <A, B> DFUPair<A, B>.toKt(): Pair<A, B> = first to second

public fun <F, S> Pair<F, S>.toDFU(): DFUPair<F, S> = DFUPair.of(first, second)