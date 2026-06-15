package dev.mayaqq.cynosure.blocks.poi

import com.google.common.collect.ImmutableSet
import dev.mayaqq.cynosure.blocks.poi.PoiHelpers.bStates
import dev.mayaqq.cynosure.blocks.poi.PoiHelpers.states
import dev.mayaqq.cynosure.mixin.accessor.PoiTypesInvoker
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.ai.village.poi.PoiManager
import net.minecraft.world.entity.ai.village.poi.PoiRecord
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import java.util.stream.Stream
import kotlin.jvm.optionals.getOrNull

public object PoiHelpers {

    @JvmField
    public val ADDITIONAL_POI_STATE_PAIRS: HashMap<BlockState, Holder<PoiType>?> = hashMapOf()

    public fun bStates(block: Block): MutableSet<BlockState> {
        return ImmutableSet.copyOf(block.stateDefinition.possibleStates)
    }

    public fun poi(vararg blocks: Block): PoiType = PoiType(states(*blocks), 0, 1)

    public fun states(vararg blocks: Block): MutableSet<BlockState> {
        return (blocks.toList().map { block -> bStates(block) }.fold(setOf<BlockState>()) {acc, set -> acc union set}) as MutableSet
    }

    public fun poiFactory(block: Block): () -> PoiType = { poi(block) }
    public fun poiFactory(vararg blocks: Block): () -> PoiType = { poi(*blocks) }

    public fun registerState(poi: PoiType) {
        PoiTypesInvoker.registerBlockStates(poi.holder()?: return, poi.matchingStates)
    }
}

public fun Block.states(): MutableSet<BlockState> = bStates(this)



public fun PoiType.key(): ResourceKey<PoiType>? = BuiltInRegistries.POINT_OF_INTEREST_TYPE.getResourceKey(this).getOrNull()
public fun ResourceKey<PoiType>.holder(): Holder<PoiType>? = BuiltInRegistries.POINT_OF_INTEREST_TYPE.getHolder(this).getOrNull()
public fun PoiType.holder(): Holder<PoiType>? = this.key()?.holder()
public fun PoiManager.inRange(poi: PoiType, pos: BlockPos, range: Int): Stream<PoiRecord> {
    return this.getInRange({it.`is`(poi.key()?: return@getInRange false)}, pos, range, PoiManager.Occupancy.ANY)
}
public fun PoiManager.anyInRange(poi: PoiType, pos: BlockPos, range: Int): Boolean = this.inRange(poi, pos, range).toList().isNotEmpty()

// Questionable Stuff starts here
public fun ResourceKey<PoiType>.add(vararg blocks: Block) = this.add(states(*blocks))
public fun ResourceKey<PoiType>.add(states: MutableSet<BlockState>) = BuiltInRegistries.POINT_OF_INTEREST_TYPE
    .getHolder(this).getOrNull().apply { states.forEach { PoiHelpers.ADDITIONAL_POI_STATE_PAIRS[it] = this } }

public fun PoiType.add(vararg blocks: Block) = this.add(states(*blocks))

public fun PoiType.add(states: MutableSet<BlockState>) {
    BuiltInRegistries.POINT_OF_INTEREST_TYPE.getResourceKey(this).getOrNull()?.add(states)
}