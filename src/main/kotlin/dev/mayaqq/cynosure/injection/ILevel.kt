package dev.mayaqq.cynosure.injection

import dev.mayaqq.cynosure.level.BlockUpdateListener
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

internal interface ILevel {

    fun cynosure_addUpdateListener(listener: BlockUpdateListener)

    fun cynosure_handleBlockUpdate(pos: BlockPos, state: BlockState)
}