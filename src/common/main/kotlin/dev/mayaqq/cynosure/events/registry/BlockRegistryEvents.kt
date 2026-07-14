package dev.mayaqq.cynosure.events.registry

import dev.mayaqq.cynosure.events.api.Event
import dev.mayaqq.cynosure.events.api.RootEventClass
import net.minecraft.world.level.block.Block

@RootEventClass
public class BedEntityBlockRegisterEvent(public val bedBlocks: List<Block> = mutableListOf()) : Event()