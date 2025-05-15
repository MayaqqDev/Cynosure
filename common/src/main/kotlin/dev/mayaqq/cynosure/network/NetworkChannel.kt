package dev.mayaqq.cynosure.network

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.core.GameInstance
import dev.mayaqq.cynosure.entities.PlayerLookup
import dev.mayaqq.cynosure.network.serialization.KByteCodec
import dev.mayaqq.cynosure.utils.set
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializerOrNull
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerChunkCache
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.LevelChunk
import kotlin.reflect.typeOf

/**
 * Create a new network channel, an alternative to the constructor providing a builder to register packets
 */
public inline fun NetworkChannel(channelId: ResourceLocation, protocolVersion: Int, builder: NetworkChannel.() -> Unit): NetworkChannel {
    val channel = NetworkChannel(channelId, protocolVersion)
    channel.builder()
    return channel
}

public class NetworkChannel(
    public val channelId: ResourceLocation,
    protocolVersion: Int
) {
    
    private val network: Network = NetworkProvider.createNetwork(channelId, protocolVersion)

    private val packets: Table<Class<*>, NetworkDirection, Network.PacketInfo<*>> = HashBasedTable.create()

    public fun <T : Any> clientbound(klass: Class<T>, codec: ByteCodec<T>, handler: ClientNetworkContext.(T) -> Unit) {
        val info = Network.PacketInfo(klass, klass.packetId ?: return, codec)
        packets[klass, NetworkDirection.CLIENTBOUND] = info
        network.registerClientboundReceiver(info, handler)
    }

    public inline fun <reified T : Any> clientbound(codec: ByteCodec<T>? = null, noinline handler: ClientNetworkContext.(T) -> Unit) {
        clientbound(T::class.java, codec ?: getByteCodecOrThrow(), handler)
    }

    public fun <T : Packet.Clientbound> clientbound(klass: Class<T>, codec: ByteCodec<T>) {
        clientbound(klass, codec, ClientNetworkContext::handlePacket)
    }

    public inline fun <reified T : Packet.Clientbound> clientbound(codec: ByteCodec<T>? = null) {
        clientbound(T::class.java, codec ?: getByteCodecOrThrow<T>())
    }

    public fun <T : Any> serverbound(klass: Class<T>, codec: ByteCodec<T>, handler: ServerNetworkContext.(T) -> Unit) {
        val info = Network.PacketInfo(klass, klass.packetId ?: return, codec)
        packets[klass, NetworkDirection.SERVERBOUND] = info
        network.registerServerboundReceiver(info, handler)
    }

    public inline fun <reified T : Any> serverbound(codec: ByteCodec<T>? = null, noinline handler: ServerNetworkContext.(T) -> Unit) {
        serverbound(T::class.java, codec ?: getByteCodecOrThrow(), handler)
    }

    public fun <T : Packet.Serverbound> serverbound(klass: Class<T>, codec: ByteCodec<T>) {
        serverbound(klass, codec, ServerNetworkContext::handlePacket)
    }

    public inline fun <reified T : Packet.Serverbound> serverbound(codec: ByteCodec<T>? = null) {
        serverbound(T::class.java, codec ?: getByteCodecOrThrow<T>())
    }

    public fun <T : Any> sendToServer(packet: T) {
        val klass = packet.javaClass
        val info = (packets[klass, NetworkDirection.SERVERBOUND] ?: error("Packet not registered $packet")) as Network.PacketInfo<T>
        network.sendToServer(info, packet)
    }

    public fun <T : Any> sendToPlayer(packet: T, player: Player) {
        if (player !is ServerPlayer) return
        val klass = packet.javaClass
        val info = (packets[klass, NetworkDirection.CLIENTBOUND] ?: error("Packet not registered $packet")) as Network.PacketInfo<T>
        network.sendToClient(player, info, packet)
    }

    public fun <T : Any> broadcast(packet: T, players: Collection<Player>) {
        for (player in players) sendToPlayer(packet, player)
    }

    public fun <T : Any> broadcast(packet: T, server: MinecraftServer = GameInstance.currentServer!!) {
        broadcast(packet, PlayerLookup.all(server))
    }

    public fun <T : Any> broadcast(packet: T, level: Level) {
        if (level.isClientSide) return
        broadcast(packet, level.players())
    }

    public fun <T : Any> broadcastInRange(packet: T, level: Level, pos: BlockPos, range: Double) {
        if (level.isClientSide) return
        for (player in level.players()) if (player.blockPosition().distSqr(pos) <= range) sendToPlayer(packet, player)
    }

    public fun <T : Any> broadcastToLoaded(packet: T, level: Level, pos: BlockPos) {
        if (level.isClientSide) return
        val chunk = level.getChunkAt(pos) as LevelChunk?
        val chunkSource = level.chunkSource as? ServerChunkCache
        if (chunk != null && chunkSource != null) {
            chunkSource.chunkMap.getPlayers(chunk.pos, false).forEach { sendToPlayer(packet, it) }
        }
    }

    public fun canSendToPlayer(player: ServerPlayer): Boolean = network.canSendToPlayer(player)

    private val Class<*>.packetId: ResourceLocation?
        get() = (getAnnotation(Packet::class.java)?.id ?: getAnnotation(SerializablePacket::class.java)?.id)
            ?.let { ResourceLocation(channelId.namespace, "${channelId.path}/$it") }


}

@PublishedApi
internal inline fun <reified T> getByteCodecOrThrow(): ByteCodec<T> =
    T::class.objectInstance?.let { ByteCodec.unit(it) }
        ?: serializerOrNull(typeOf<T>())?.cast<T>()?.let(::KByteCodec)
        ?: error("You have to pass a custom bytecodec for non-serializable packets with data: ${T::class}")

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@PublishedApi
internal inline fun <T> KSerializer<*>.cast(): KSerializer<T> = this as KSerializer<T>

@Suppress("NOTHING_TO_INLINE")
internal inline fun ClientNetworkContext.handlePacket(packet: Packet.Clientbound) {
    packet.run { this@handlePacket.handle() }
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun ServerNetworkContext.handlePacket(packet: Packet.Serverbound) {
    packet.run { this@handlePacket.handle() }
}