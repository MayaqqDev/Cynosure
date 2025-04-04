package dev.mayaqq.cynosure.network

import dev.mayaqq.cynosure.network.base.ClientBoundPacketType
import dev.mayaqq.cynosure.network.base.Network
import dev.mayaqq.cynosure.network.base.ServerBoundPacketType
import io.netty.buffer.Unpooled
import kotlinx.coroutines.*
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.vehicle.Minecart

public class FabricClientNetwork(public val channel: ResourceLocation) : Network {
    override fun <T : Packet<T>> register(type: ClientBoundPacketType<T>) {
        ClientPlayNetworking.registerGlobalReceiver(createChannelLocation(channel, type.id)) { client, handler, buf, responseSender ->
            val decode = type.decode(buf)
            type.run { decode.handle() }
        }
    }

    override fun <T : Packet<T>> register(type: ServerBoundPacketType<T>) {
        throw IllegalStateException("Cannot register server bound packets on the client")
    }

    override fun <T : Packet<T>> sendToServer(packet: T) {
        val type = packet.type
        val buf = FriendlyByteBuf(Unpooled.buffer())
        type.encode(packet, buf)
        ClientPlayNetworking.send(createChannelLocation(channel, type.id), buf)
    }

    override fun <T : Packet<T>> sendToClient(packet: T, player: ServerPlayer) {
        throw IllegalStateException("Cannot send packets to a specific player on the client")
    }

    override fun canSendToPlayer(player: ServerPlayer): Boolean {
        throw IllegalStateException("Cannot send packets to a specific player on the client")
    }

    private companion object {
        fun createChannelLocation(channel: ResourceLocation, id: ResourceLocation) = ResourceLocation(channel.namespace, "${channel.path}/${id.namespace}/${id.path}")
    }
}