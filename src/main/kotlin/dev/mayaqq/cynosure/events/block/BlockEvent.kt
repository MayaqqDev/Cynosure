package dev.mayaqq.cynosure.events.block

import dev.mayaqq.cynosure.events.api.CancellableEvent
import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.state.BlockState

public sealed class BlockEvent(
    public val level: LevelAccessor,
    public val state: BlockState,
    public val pos: BlockPos
) : CancellableEvent() {

    public class Place(
        level: LevelAccessor,
        state: BlockState,
        pos: BlockPos,
        public val placer: Entity?
    ) : BlockEvent(level, state, pos)

    public class Break(
        level: LevelAccessor,
        state: BlockState,
        pos: BlockPos,
        public val player: Player?
    ) : BlockEvent(level, state, pos)

    public class FluidConversion(
        level: LevelAccessor,
        state: BlockState,
        pos: BlockPos
    ) : BlockEvent(level, state, pos)
}

public sealed class FallingBlockEvent(
    public val level: Level,
    public open val fallingState: BlockState,
    public val entity: FallingBlockEntity
) : Event {

    public class Fall(
        level: Level,
        fallingState: BlockState,
        entity: FallingBlockEntity,
        public val blockPos: BlockPos,
    ) : FallingBlockEvent(level, fallingState, entity)

    public class Tick(
        level: Level,
        fallingState: BlockState,
        entity: FallingBlockEntity
    ) : FallingBlockEvent(level, fallingState, entity)

    public class Land(
        level: Level,
        fallingState: BlockState,
        entity: FallingBlockEntity,
        public val blockPos: BlockPos,
        public val landingOn: BlockState
    ) : FallingBlockEvent(level, fallingState, entity) {

        /**
         * Custom block state placed by the falling block, setting this also cancels the event
         */
        public var placedState: BlockState? = null

        override val isCancelled: Boolean
            get() = placedState != null
    }
}