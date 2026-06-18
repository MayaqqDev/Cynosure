package dev.mayaqq.cynosure.core.bytecodecs

import com.teamresourceful.bytecodecs.base.ByteCodec
import io.netty.buffer.ByteBuf
import net.minecraft.core.RegistryAccess
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import java.util.function.Function

fun <B : ByteBuf, T> StreamCodec<B, T>.toByteCodec(mapper: Function<ByteBuf, B>): ByteCodec<T> {
    return ByteCodec.passthrough<T>(
        { buf: ByteBuf, value: T -> this.encode(mapper.apply(buf), value) },
        { buffer: ByteBuf -> this.decode(mapper.apply(buffer)) }
    )
}

private fun <B : ByteBuf, T> ByteCodec<T>.toStreamCodec(mapper: Function<B, ByteBuf>): StreamCodec<B, T> {
    return StreamCodec.of<B, T>(
        { buf: B, value: T -> this.encode(value, mapper.apply(buf)) },
        { buf: B -> this.decode(mapper.apply(buf)) }
    )
}

fun <T> StreamCodec<ByteBuf, T>.toByteCodec(): ByteCodec<T> {
    return this.toByteCodec(Function.identity())
}

fun <T> ByteCodec<T>.toStreamCodec(): StreamCodec<ByteBuf, T> {
    return this.toStreamCodec(Function.identity())
}

fun <T> StreamCodec<RegistryFriendlyByteBuf, T>.toByteCodec(): ByteCodec<T> {
    return this.toByteCodec { it.toRegistry() }
}

fun ByteBuf.toRegistry(): RegistryFriendlyByteBuf {
    if (this is RegistryFriendlyByteBuf) return this
    return RegistryFriendlyByteBuf(this, getRegistryAccess())
}

expect fun getRegistryAccess(): RegistryAccess