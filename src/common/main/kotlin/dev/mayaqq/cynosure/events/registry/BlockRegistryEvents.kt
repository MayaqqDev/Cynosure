package dev.mayaqq.cynosure.events.registry

import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.world.level.block.Block

public class BedEntityBlockRegisterEvent(public val bedBlocks: List<Block> = mutableListOf()) : Event()