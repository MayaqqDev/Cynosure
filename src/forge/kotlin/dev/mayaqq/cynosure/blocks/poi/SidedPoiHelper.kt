package dev.mayaqq.cynosure.blocks.poi

import com.google.common.collect.ImmutableSet
import dev.mayaqq.cynosure.core.Loader
import dev.mayaqq.cynosure.core.currentLoader
import dev.mayaqq.cynosure.forge.mixin.ForgePoiTypesAccessor
import dev.mayaqq.cynosure.mixin.accessor.PoiTypeAccessor
import dev.mayaqq.cynosure.mixin.accessor.PoiTypesInvoker
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.level.block.state.BlockState

public object SidedPoiHelperImpl : SidedPoiHelper {
    override fun add(type: PoiType, states: MutableSet<BlockState>) {
        (type as PoiTypeAccessor).`cynosure$setBlockStates`(ImmutableSet.builder<BlockState?>()
            .addAll(type.matchingStates)
            .addAll(states)
            .build()
        )
        states.forEach {
            ForgePoiTypesAccessor.`cynosure$getBlockStates`().put(it, type)
        }
    }

}