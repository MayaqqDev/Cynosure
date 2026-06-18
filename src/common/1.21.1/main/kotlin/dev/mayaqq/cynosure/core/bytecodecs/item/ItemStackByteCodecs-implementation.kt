package dev.mayaqq.cynosure.core.bytecodecs.item

import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.core.bytecodecs.toByteCodec
import net.minecraft.world.item.ItemStack

public actual object ItemStackByteCodec : ByteCodec<ItemStack> by ItemStack.STREAM_CODEC.toByteCodec()