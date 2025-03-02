package dev.mayaqq.cynosure.network.defaults

import dev.mayaqq.cynosure.network.Packet
import dev.mayaqq.cynosure.network.base.ClientBoundPacketType
import dev.mayaqq.cynosure.network.base.PacketType
import dev.mayaqq.cynosure.network.base.ServerBoundPacketType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.repository.Pack
import net.minecraft.world.entity.player.Player
import java.util.function.Supplier

public interface DatalessPacketType<T : Packet<T>> : PacketType<T> {
    public val instance: T

    override fun encode(packet: T, buffer: FriendlyByteBuf) {
        // Do nothing :3
    }

    override fun decode(buffer: FriendlyByteBuf): T = instance

    public abstract class Client<T : Packet<T>> @PublishedApi internal constructor(override val type: Class<T>, override val id: ResourceLocation, override val instance: T) : ClientBoundPacketType<T>, DatalessPacketType<T>

    public abstract class Server<T : Packet<T>> @PublishedApi internal constructor(override val type: Class<T>, override val id: ResourceLocation, override val instance: T) : ServerBoundPacketType<T>, DatalessPacketType<T>

    public companion object {

        public inline fun <reified T : Packet<T>> createClientBound(id: ResourceLocation, instance: T, crossinline handler: () -> Unit): Client<T> =
            object : Client<T>(T::class.java, id, instance) {
                override fun handle(packet: T) {
                    handler()
                }
            }

        public inline fun <reified T : Packet<T>> createServerBound(id: ResourceLocation, instance: T, crossinline handler: (Player) -> Unit): Server<T> =
            object : Server<T>(T::class.java, id, instance) {
                override fun handle(player: Player, packet: T) {
                    handler(player)
                }
            }
    }
}