package dev.mayaqq.cynosure.network

import dev.mayaqq.cynosure.network.base.ClientBoundPacketType
import dev.mayaqq.cynosure.network.base.Network
import dev.mayaqq.cynosure.network.base.ServerBoundPacketType
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerChunkCache
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import java.util.function.BooleanSupplier

public class NetworkChannel(
    modId: String,
    protocolVersion: Int,
    channel: String,
    optional: () -> Boolean
) : Network {

    private val network: Network = getNetwork(ResourceLocation(modId, channel), protocolVersion, optional)

    public constructor(
        modId: String,
        protocolVersion: Int,
        channel: String
    ) : this(modId, protocolVersion, channel, { false })

    public constructor(
        modId: String,
        protocolVersion: Int,
        channel: String,
        optional: Boolean
    ) : this(modId, protocolVersion, channel, { optional })


    override fun <T : Packet<T>> register(type: ClientBoundPacketType<T>) {
        network.register(type)
    }

    override fun <T : Packet<T>> register(type: ServerBoundPacketType<T>) {
        network.register(type)
    }

    override fun <T : Packet<T>> sendToServer(packet: T) {
        this.network.sendToServer(packet)
    }

    override fun <T : Packet<T>> sendToClient(packet: T, player: ServerPlayer) {
        this.network.sendToClient(packet, player)
    }

    public fun <T: Packet<T>> sendToClient(packet: T, player: Player) {
        if (player is ServerPlayer) {
            this.sendToClient(packet, player)
        }
    }

    public fun <T : Packet<T>> sendToClients(packet: T, players: Iterable<Player>) {
        players.forEach { this.sendToClient(packet, it) }
    }

    public fun <T : Packet<T>> sendToClients(packet: T, vararg players: Player) {
        players.forEach { this.sendToClient(packet, it) }
    }

    public fun <T : Packet<T>> sendToAllClients(packet: T, server: MinecraftServer) {
        this.sendToClients(packet, server.playerList.players)
    }

    public fun <T : Packet<T>> sendToAllClients(packet: T, server: MinecraftServer, except: Player) {
        this.sendToClients(packet, server.playerList.players.filter { it != except })
    }

    public fun <T : Packet<T>> sendToClientsInLevel(packet: T, level: Level) {
        this.sendToClients(packet, level.players())
    }

    public fun <T : Packet<T>> sendToAllInChunk(packet: T, level: Level, pos: BlockPos) {
        level.getChunkAt(pos)?.let {
            if (level.chunkSource is ServerChunkCache) {
                val chunkSource = level.chunkSource as ServerChunkCache
                chunkSource.chunkMap.getPlayers(it.pos, false).forEach { this.sendToClient(packet, it) }
            }
        }
    }

    public fun <T : Packet<T>> sendToInRange(packet: T, level: Level, pos: BlockPos, range: Double) {
        sendToClients(packet, level.players().filter { it.blockPosition().distSqr(pos) <= range })
    }

    override fun canSendToPlayer(player: ServerPlayer): Boolean {
        return this.network.canSendToPlayer(player)
    }

    public fun canSendToPlayer(player: Player): Boolean {
        return player is ServerPlayer && this.canSendToPlayer(player)
    }
}

public expect fun getNetwork(channel: ResourceLocation, protocolVersion: Int, optional: () -> Boolean): Network