package dev.mayaqq.cynosure.events.registry

import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity

public class VanillaBlockEntityRegistrationEvent<T : BlockEntity> @JvmOverloads constructor(
    public val entityClass: Class<T>,
    public val blocks: MutableSet<Block> = mutableSetOf()
) : Event()