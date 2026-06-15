package dev.mayaqq.cynosure.core.bytecodecs

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.Lifecycle
import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.utils.result.success
import dev.mayaqq.cynosure.utils.result.toDataResult
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.nbt.NbtOps
import net.minecraft.network.FriendlyByteBuf
import java.nio.ByteBuffer

/**
 * Get a [FriendlyByteBuf] from a [ByteBuf], will cast it if its already an instance of [FriendlyByteBuf]
 * @return Either [this] if its an instance of [FriendlyByteBuf], otherwise a new [FriendlyByteBuf] wrapping [this]
 */
public fun ByteBuf.friendzone(): FriendlyByteBuf =
    if (this is FriendlyByteBuf) this else FriendlyByteBuf(this)

/**
 * Create a byte codec from this codec.
 *
 * NOTE: It is recommended to make your own byte codecs instead, as the resulting
 * bytecodec is not very efficient
 * @return  a [CodecByteCodec] that can encode/decode values of type [A]
 */
public fun <A : Any> Codec<A>.toByteCodec(): CodecByteCodec<A> = CodecByteCodec(this)

/**
 * Create a codec from a byte codec, that encodes the resulying bytes directly
 *
 * NOTE: It is recommended to make your own codec instead, as the resulting codec doesn't produce
 * very readable data
 * @return a [ByteCodecCodec] that can encode/decode values of type [A]
 */
public fun <A> ByteCodec<A>.toCodec(): ByteCodecCodec<A> = ByteCodecCodec(this)

/**
 * A bytecodec backed by a codec. See [toByteCodec]
 */
public class CodecByteCodec<A : Any> internal constructor(
    public val codec: Codec<A>
) : FriendlyByteCodec<A> {
    override fun encodeFriendly(value: A, buf: FriendlyByteBuf) {
        buf.writeJsonWithCodec(codec, value)
    }

    override fun decodeFriendly(buf: FriendlyByteBuf): A = buf.readJsonWithCodec(codec)
}

/**
 * A codec backed by a bytecodec. See [toCodec]
 */
public class ByteCodecCodec<A> internal constructor(
    public val byteCodec: ByteCodec<A>
) : Codec<A> {
    override fun <T : Any> encode(input: A, ops: DynamicOps<T>, prefix: T): DataResult<T> {
        val buf = Unpooled.buffer()
        byteCodec.encode(input, buf)
        val buffer = ByteBuffer.allocate(buf.capacity())
        buf.readBytes(buffer)
        return ops.createByteList(buffer).success().toDataResult()
    }

    override fun <T : Any> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<A, T>> {
        return ops.getByteBuffer(input).setLifecycle(Lifecycle.stable()).map {
            val buf = Unpooled.buffer()
            buf.writeBytes(it)
            Pair.of(byteCodec.decode(buf), ops.empty())
        }
    }
}