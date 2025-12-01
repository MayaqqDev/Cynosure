package dev.mayaqq.cynosure.blocks

import dev.mayaqq.cynosure.core.extensions.Extension
import dev.mayaqq.cynosure.core.extensions.ExtensionRegistry
import net.minecraft.world.level.block.Block

public interface BlockExtension : Extension<Block> {

    public companion object Registry : ExtensionRegistry<Block, BlockExtension>(
        Block::class.java, BlockExtension::class.java
    )
}