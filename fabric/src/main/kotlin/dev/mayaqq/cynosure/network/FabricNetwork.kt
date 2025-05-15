package dev.mayaqq.cynosure.network

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

internal class FabricNetwork(
    private val channel: ResourceLocation,
    private val protocolVersion: Int
) : Network {

    val client: FabricClientNetwork? =
        if(FabricLoader.getInstance().environmentType == EnvType.CLIENT) FabricClientNetwork() else null

    private fun ResourceLocation.withProtocolVersion() = ResourceLocation(namespace, "$path/v$protocolVersion")

    override fun <T : Any> sendToClient(client: ServerPlayer, info: Network.PacketInfo<T>, packet: T) {
        FabricServerNetwork.send(client, info.id.withProtocolVersion(), info.codec, packet)
    }

    override fun <T : Any> sendToServer(info: Network.PacketInfo<T>, packet: T) {
        client?.send(info.id.withProtocolVersion(), info.codec, packet)
    }

    override fun <T : Any> registerClientboundReceiver(
        info: Network.PacketInfo<T>,
        handler: ClientNetworkContext.(T) -> Unit
    ) {
        client?.register(info.id.withProtocolVersion(), info.codec, handler)
    }

    override fun <T : Any> registerServerboundReceiver(
        info: Network.PacketInfo<T>,
        handler: ServerNetworkContext.(T) -> Unit
    ) {
        FabricServerNetwork.register(info.id.withProtocolVersion(), info.codec, handler)
    }

    override fun canSendToPlayer(player: ServerPlayer): Boolean {
        ServerPlayNetworking.getSendable(player.connection).forEach { location ->
            if (location.namespace.equals(channel.namespace) && location.path.startsWith(channel.path)) {
                return true
            }
        }
        return false
    }
}


public class NetworkProviderImpl : NetworkProvider {
    override fun createNetwork(networkId: ResourceLocation, protocolVersion: Int): Network {
        return FabricNetwork(networkId, protocolVersion)
    }
}