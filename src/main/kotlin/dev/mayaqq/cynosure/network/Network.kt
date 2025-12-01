package dev.mayaqq.cynosure.network

import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.internal.loadPlatform
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

public interface Network {

    public fun <T : Any> sendToClient(client: ServerPlayer, info: PacketInfo<T>, packet: T)

    public fun <T : Any> sendToServer(info: PacketInfo<T>, packet: T)

    public fun <T : Any> registerClientboundReceiver(
        info: PacketInfo<T>,
        handler: ClientNetworkContext.(T) -> Unit
    )

    public fun <T : Any> registerServerboundReceiver(
        info: PacketInfo<T>,
        handler: ServerNetworkContext.(T) -> Unit
    )

    public fun canSendToPlayer(player: ServerPlayer): Boolean

    public data class PacketInfo<T : Any>(
        public val clazz: Class<T>,
        public val id: ResourceLocation,
        public val codec: ByteCodec<T>
    )
}

@CynosureInternal
public interface NetworkProvider {

    public companion object Impl : NetworkProvider by loadPlatform()

    public fun createNetwork(networkId: ResourceLocation, protocolVersion: Int): Network
}

public sealed class NetworkContext(public val direction: NetworkDirection) {

    public abstract fun execute(action: () -> Unit)

}

public enum class NetworkDirection {
    CLIENTBOUND,
    SERVERBOUND
}

public abstract class ServerNetworkContext(
    public val server: MinecraftServer,
    public val sender: ServerPlayer
) : NetworkContext(NetworkDirection.SERVERBOUND)


public abstract class ClientNetworkContext : NetworkContext(NetworkDirection.CLIENTBOUND)

