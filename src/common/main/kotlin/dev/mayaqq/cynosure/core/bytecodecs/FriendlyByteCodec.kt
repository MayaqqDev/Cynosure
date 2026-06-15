package dev.mayaqq.cynosure.core.bytecodecs

import com.teamresourceful.bytecodecs.base.ByteCodec
import io.netty.buffer.ByteBuf
import net.minecraft.network.FriendlyByteBuf

public inline fun <T> FriendlyByteCodec(crossinline encoder: (T, FriendlyByteBuf) -> Unit, crossinline decoder: (FriendlyByteBuf) -> T): FriendlyByteCodec<T> {
    return object : FriendlyByteCodec<T> {
        override fun encodeFriendly(value: T, buf: FriendlyByteBuf) {
            encoder(value, buf)
        }

        override fun decodeFriendly(buf: FriendlyByteBuf): T = decoder(buf)
    }
}

public interface FriendlyByteCodec<T> : ByteCodec<T> {

    override fun encode(value: T, buffer: ByteBuf) {
        encodeFriendly(value, buffer.friendzone())
    }

    override fun decode(buffer: ByteBuf): T = decodeFriendly(buffer.friendzone())

    public fun encodeFriendly(value: T, buf: FriendlyByteBuf)

    public fun decodeFriendly(buf: FriendlyByteBuf): T
}