/*
 * Code and models are taken and modified from the BetterBeds mod by TeamMidnightDust
 * The code falls under the MIT license, thanks TeamMidnightDust!
 */
package dev.mayaqq.cynosure.blocks.model

import dev.mayaqq.cynosure.blocks.poi.add
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.ai.village.poi.PoiTypes
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.BedBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

/**
 * A model-based Bed Block class that uses block models instead of a Block Entity Renderer.
 * This class also automatically registers the bed as a villager Poi.
 *
 * To use the model system, configure your blockstates JSON to something like this:
 * ```
 * {
 *   "variants": {
 *     "facing=east,part=head":  { "model": "example:block/bed/example_bed_head", "y": 270 },
 *     "facing=north,part=head": { "model": "example:block/bed/example_bed_head", "y": 180 },
 *     "facing=south,part=head": { "model": "example:block/bed/example_bed_head" },
 *     "facing=west,part=head":  { "model": "example:block/bed/example_bed_head", "y": 90 },
 *
 *     "facing=east,part=foot":  { "model": "example:block/bed/example_bed_foot", "y": 270 },
 *     "facing=north,part=foot": { "model": "example:block/bed/example_bed_foot", "y": 180 },
 *     "facing=south,part=foot": { "model": "example:block/bed/example_bed_foot" },
 *     "facing=west,part=foot":  { "model": "example:block/bed/example_bed_foot", "y": 90 }
 *   }
 * }
 * ```
 * and your block models like this:
 *
 * `assets/example/models/block/bed/example_bed_head.json`
 * ```
 * {
 *   "parent": "cynosure:block/bed/template_bed_head",
 *   "textures": {
 *     "bed": "example:block/bed/example_bed_texture"
 *   }
 * }
 * ```
 * `assets/example/models/block/bed/example_bed_foot.json`
 * ```
 * {
 *   "parent": "cynosure:block/bed/template_bed_foot",
 *   "textures": {
 *     "bed": "example:block/bed/example_bed_texture"
 *   }
 * }
 * ```
 */


public class ModelBedBlock(properties: Properties, color: DyeColor? = null) : BedBlock(color, properties) {
    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
    override fun skipRendering(state: BlockState, neigborState: BlockState, offset: Direction): Boolean = neigborState.block is BedBlock

    override fun getShadeBrightness(state: BlockState, level: BlockGetter, pos: BlockPos): Float = 1.0F
}