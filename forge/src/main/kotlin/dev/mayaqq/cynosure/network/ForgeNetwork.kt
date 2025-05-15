package dev.mayaqq.cynosure.network

import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.network.NetworkEvent.Context
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel


internal class ForgeNetwork(
    name: ResourceLocation,
    protocolVersion: Int
) : Network {

    private val channel: SimpleChannel = NetworkRegistry.newSimpleChannel(
        name,
        { protocolVersion.toString() },
        { it == protocolVersion.toString() },
        { it == protocolVersion.toString() }
    )

    private var packets: Int = 0

    override fun <T : Any> registerClientboundReceiver(
        info: Network.PacketInfo<T>,
        handler: ClientNetworkContext.(T) -> Unit
    ) {
        @Suppress("INACCESSIBLE_TYPE")
        channel.registerMessage(
            packets++,
            info.clazz,
            info.codec::encode,
            info.codec::decode
        ) { packet, contextSupplier ->
            val ctx = contextSupplier.get()
            ClientContext(ctx).handler(packet)
            ctx.packetHandled = true
        }
    }

    override fun <T : Any> registerServerboundReceiver(
        info: Network.PacketInfo<T>,
        handler: ServerNetworkContext.(T) -> Unit
    ) {
        @Suppress("INACCESSIBLE_TYPE")
        channel.registerMessage(
            packets++,
            info.clazz,
            info.codec::encode,
            info.codec::decode
        ) { packet, contextSupplier ->
            val ctx = contextSupplier.get()
            ServerContext(ctx).handler(packet)
            ctx.packetHandled = true
        }
    }

    override fun <T : Any> sendToClient(client: ServerPlayer, info: Network.PacketInfo<T>, packet: T) {
        channel.send(PacketDistributor.PLAYER.with { client }, packet)
    }

    override fun <T : Any> sendToServer(info: Network.PacketInfo<T>, packet: T) {
        channel.sendToServer(packet)
    }

    override fun canSendToPlayer(player: ServerPlayer): Boolean = true


    private class ClientContext(val ctx: Context) : ClientNetworkContext() {
        override fun execute(action: () -> Unit) {
            ctx.enqueueWork(action)
        }
    }

    private class ServerContext(val ctx: Context) : ServerNetworkContext(
        ctx.sender!!.server, ctx.sender!!
    ) {
        override fun execute(action: () -> Unit) {
            ctx.enqueueWork(action)
        }
    }

}


public class NetworkProviderImpl : NetworkProvider {
    override fun createNetwork(networkId: ResourceLocation, protocolVersion: Int): Network {
        return ForgeNetwork(networkId, protocolVersion)
    }
}

