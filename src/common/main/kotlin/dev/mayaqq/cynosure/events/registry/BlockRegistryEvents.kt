package dev.mayaqq.cynosure.events.registry

import dev.mayaqq.cynosure.events.api.Event
import invoke.kitty.kritter.registry.api.entry.RegistryEntry
import net.minecraft.world.level.block.Block

public class VanillaBlockEntityRegistrationEvent(
    public val name: String
) : Event() {

    public fun add(block: Block) {
        blocks.add(block)
    }

    public fun add(block: RegistryEntry<Block>) {
        blocks.add(block.get())
    }

    public val blocks: MutableSet<Block> = mutableSetOf()
}