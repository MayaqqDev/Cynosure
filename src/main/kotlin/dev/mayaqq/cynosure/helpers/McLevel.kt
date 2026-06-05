package dev.mayaqq.cynosure.helpers

import dev.mayaqq.cynosure.client.enviroment.ClientOnly
import dev.mayaqq.cynosure.client.enviroment.ClientOnlyException
import invoke.kitty.kritter.platform.ENVIRONMENT
import invoke.kitty.kritter.platform.Side
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

@ClientOnly
public val McLevel: Level? get() = if (ENVIRONMENT == Side.CLIENT) McClient.level else throw ClientOnlyException()

private val mutablePos = BlockPos.MutableBlockPos()

public operator fun Level.get(pos: BlockPos): BlockState = this.getBlockState(pos)
public operator fun Level.get(x: Int, y: Int, z: Int): BlockState = this.getBlockState(mutablePos.set(x, y, z))