package dev.mayaqq.cynosure.network.serialization

import com.teamresourceful.bytecodecs.base.ByteCodec
import io.netty.buffer.ByteBuf

public inline fun <T> ByteCodec(crossinline serialize: (T, ByteBuf) -> Unit, crossinline  deserialize: (ByteBuf) -> T): ByteCodec<T> = object : ByteCodec<T> {
    override fun encode(p0: T, p1: ByteBuf) {
        serialize(p0, p1)
    }

    override fun decode(p0: ByteBuf): T = deserialize(p0)
}