package dev.mayaqq.cynosure.core.codecs

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps

public class AlternativesCodec<A>(
    private val codecs: List<Codec<A>>
) : Codec<A> {

    override fun <T : Any> encode(input: A, ops: DynamicOps<T>, prefix: T): DataResult<T> {
        for (codec in codecs) {
            val r = codec.encode(input, ops, prefix)
            if (r.result().isPresent) return r
        }
        return DataResult.error { "No codecs found" }
    }

    override fun <T : Any> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<A, T>> {
        for (codec in codecs) {
            val r = codec.decode(ops, input)
            if (r.result().isPresent) return r
        }

        return  DataResult.error { "No codecs found" }
    }
}