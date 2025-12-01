package dev.mayaqq.cynosure.core.codecs

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps

public class RecursiveCodec<T> internal constructor(private val name: String, wrapped: (Codec<T>) -> Codec<T>) : Codec<T> {

    private val wrapped: Codec<T> by lazy { wrapped(this) }

    override fun <S> decode(ops: DynamicOps<S>, input: S): DataResult<Pair<T, S>> {
        return wrapped.decode(ops, input)
    }

    override fun <S> encode(input: T, ops: DynamicOps<S>, prefix: S): DataResult<S> {
        return wrapped.encode(input, ops, prefix)
    }

    override fun toString(): String {
        return "RecursiveCodec[$name]"
    }
}