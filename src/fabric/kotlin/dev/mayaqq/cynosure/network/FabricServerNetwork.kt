package dev.mayaqq.cynosure.network

import com.teamresourceful.bytecodecs.base.ByteCodec
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer


internal typealias Serverpackethandler<T> = ServerNetworkContext.(T) -> Unit

internal object FabricServerNetwork {

    fun <T : Any> send(to: ServerPlayer, type: ResourceLocation, codec: ByteCodec<T>, packet: T) {
        val buf = FriendlyByteBuf(Unpooled.buffer())
        codec.encode(packet, buf)
        ServerPlayNetworking.send(to, type, buf)
    }

    fun <T : Any> register(id: ResourceLocation, codec: ByteCodec<T>, handler: Serverpackethandler<T>) {
        ServerPlayNetworking.registerGlobalReceiver(id) { minecraftServer, serverPlayer, serverGamePacketListenerImpl, friendlyByteBuf, packetSender ->
            val packet = codec.decode(friendlyByteBuf)
            Context(minecraftServer, serverPlayer).handler(packet)
        }
    }

    private class Context(server: MinecraftServer, sender: ServerPlayer) : ServerNetworkContext(server, sender) {
        override fun execute(action: () -> Unit) {
            server.execute(action)
        }
    }
}