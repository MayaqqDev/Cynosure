package dev.mayaqq.cynosure.level

import dev.mayaqq.cynosure.injection.ILevel
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

/**
 * Interface providing the ability to listen to block updates of specific positions.
 */
public interface BlockUpdateListener {

    /**
     * The block positions this update listener listens to
     */
    public val listenedPositions: Iterable<BlockPos>

    /**
     * Called after each update before [onBlockUpdate], if this returns true the listener will be removed from the level
     */
    public fun shouldRemove(level: ServerLevel): Boolean

    /**
     * Handle block updates here. Note that the provided [BlockPos] and [BlockState] are of the
     * block update, not the listener itself
     * @param level [Level] in which the update happened
     * @param pos [BlockPos] of the block update
     * @param state [BlockState] at the updated position
     */
    public fun onBlockUpdate(level: ServerLevel, pos: BlockPos, state: BlockState)

}

public fun Level.addUpdateListener(listener: BlockUpdateListener) {
    (this as ILevel).cynosure_addUpdateListener(listener)
}