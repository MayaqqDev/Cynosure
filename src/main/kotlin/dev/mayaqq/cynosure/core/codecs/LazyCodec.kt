package dev.mayaqq.cynosure.core.codecs

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps

public class LazyCodec<A>(initializer: () -> Codec<A>) : Codec<A> {

    private val codec by lazy(LazyThreadSafetyMode.PUBLICATION, initializer)

    override fun <T : Any> encode(input: A, ops: DynamicOps<T>, prefix: T): DataResult<T> =
        codec.encode(input, ops, prefix)

    override fun <T : Any> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<A, T>> =
        codec.decode(ops, input)

}