package dev.mayaqq.cynosure.network.defaults

import com.mojang.serialization.Codec
import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.network.Packet
import dev.mayaqq.cynosure.network.base.ClientBoundPacketType
import dev.mayaqq.cynosure.network.base.PacketType
import dev.mayaqq.cynosure.network.base.ServerBoundPacketType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.repository.Pack
import net.minecraft.world.entity.animal.Cod
import net.minecraft.world.entity.player.Player
import kotlin.reflect.KClass

public interface CodecPacketType<T : Packet<T>> : PacketType<T> {
    public val codec: ByteCodec<T>

    override fun encode(packet: T, buffer: FriendlyByteBuf) {
        codec.encode(packet, buffer)
    }

    override fun decode(buffer: FriendlyByteBuf): T = codec.decode(buffer)

    public abstract class Client<T : Packet<T>> @PublishedApi internal constructor(override val type: Class<T>, override val id: ResourceLocation, override val codec: ByteCodec<T>) : ClientBoundPacketType<T>, CodecPacketType<T>

    public abstract class Server<T : Packet<T>> @PublishedApi internal constructor(override val type: Class<T>, override val id: ResourceLocation, override val codec: ByteCodec<T>) : ServerBoundPacketType<T>, CodecPacketType<T>


    public companion object {

        public inline fun <reified T : Packet<T>> clientBound(
            id: ResourceLocation,
            codec: ByteCodec<T>,
            crossinline handler: T.() -> Unit
        ) = object : Client<T>(T::class.java, id, codec) {
            override fun handle(packet: T) = packet.handler()
        }

        public inline fun <reified T : Packet<T>> serverBound(
            id: ResourceLocation,
            codec: ByteCodec<T>,
            crossinline handler: T.(player: Player) -> Unit
        ) = object : Server<T>(T::class.java, id, codec) {
            override fun handle(player: Player, packet: T) = packet.handler(player)
        }
    }
}