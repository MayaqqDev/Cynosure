package dev.mayaqq.cynosure.events.registry

import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.world.level.block.Block

public class VanillaBlockEntityRegistrationEvent @JvmOverloads constructor(
    public val entityClass: Class<*>,
    public val blocks: MutableSet<Block> = mutableSetOf()
) : Event()