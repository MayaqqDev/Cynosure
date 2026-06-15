package dev.mayaqq.cynosure.core.bytecodecs

import com.teamresourceful.bytecodecs.base.ByteCodec
import io.netty.buffer.ByteBuf

public class LazyByteCodec<T>(initializer: () -> ByteCodec<T>) : ByteCodec<T> {

    private val codec: ByteCodec<T> by lazy(initializer)

    override fun encode(value: T, buffer: ByteBuf) {
        codec.encode(value, buffer)
    }

    override fun decode(buffer: ByteBuf): T = codec.decode(buffer)
}