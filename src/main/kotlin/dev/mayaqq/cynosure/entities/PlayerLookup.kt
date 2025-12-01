package dev.mayaqq.cynosure.entities

import dev.mayaqq.cynosure.mixin.accessor.ChunkMapAccessor
import dev.mayaqq.cynosure.mixin.accessor.TrackedEntityAccessor
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ChunkMap
import net.minecraft.server.level.ServerChunkCache
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerPlayerConnection
import net.minecraft.server.players.PlayerList
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.entity.BlockEntity
import java.util.*

/*
 * This code includes modifications based on or derived from code provided by fabric-api.
 * The original code can be found at: https://github.com/FabricMC/fabric
 * fabric-api is licensed under Apache License 2.0.
 */
public object PlayerLookup {

    // Effectively just a null safe call to server.playerList.getPlayers
    @Suppress("UNNECESSARY_SAFE_CALL") // It can actually be null but mc doesnt mark it as nullable
    public fun all(server: MinecraftServer): Collection<ServerPlayer> =
        server.playerList?.let(PlayerList::getPlayers) ?: emptyList()

    public fun tracking(level: ServerLevel, pos: ChunkPos): Collection<ServerPlayer> =
        level.chunkSource.chunkMap.getPlayers(pos, false)

    public fun tracking(level: ServerLevel, pos: BlockPos): Collection<ServerPlayer> =
        tracking(level, ChunkPos(pos))

    public fun tracking(blockEntity: BlockEntity): Collection<ServerPlayer> {
        if (!blockEntity.hasLevel()) return emptyList()
        return if (blockEntity.level?.isClientSide == true) tracking(blockEntity.level as ServerLevel, blockEntity.blockPos)
        else error("Only supported on server worlds!")
    }

    public fun tracking(entity: Entity): Collection<ServerPlayer> {
        val manager = entity.level().chunkSource

        if (manager is ServerChunkCache) {
            val storage = manager.chunkMap
            val tracker = storage.getEntityMap().get(entity.id)
            return tracker?.getPlayersTracking()
                ?.map(ServerPlayerConnection::getPlayer)
                ?.let(Collections::unmodifiableList)
                ?: emptyList()
        }

        error("Only supported on server worlds!")
    }
}

public fun ChunkMap.getEntityMap(): Int2ObjectMap<TrackedEntityAccessor> = (this as ChunkMapAccessor).getEntityMap()