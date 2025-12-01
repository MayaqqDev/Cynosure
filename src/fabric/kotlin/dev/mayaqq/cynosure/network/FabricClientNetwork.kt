package dev.mayaqq.cynosure.network

import com.teamresourceful.bytecodecs.base.ByteCodec
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation

internal typealias Clientpackethandler<T> = ClientNetworkContext.(T) -> Unit

internal class FabricClientNetwork {

    fun <T : Any> send(type: ResourceLocation, codec: ByteCodec<T>, packet: T) {
        val buf = FriendlyByteBuf(Unpooled.buffer())
        codec.encode(packet, buf)
        ClientPlayNetworking.send(type, buf)
    }

    fun <T : Any> register(id: ResourceLocation, codec: ByteCodec<T>, handler: Clientpackethandler<T>) {
        ClientPlayNetworking.registerGlobalReceiver(id) { minecraft, clientPacketListener, friendlyByteBuf, packetSender ->
            val packet = codec.decode(friendlyByteBuf)
            Context.handler(packet)
        }
    }

    private object Context : ClientNetworkContext() {
        override fun execute(action: () -> Unit) {
            Minecraft.getInstance().execute(action)
        }
    }
}