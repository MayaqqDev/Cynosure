package dev.mayaqq.cynosure.core.bytecodecs.item

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import dev.mayaqq.cynosure.core.bytecodecs.ByteCodecs
import dev.mayaqq.cynosure.core.bytecodecs.LazyByteCodec
import dev.mayaqq.cynosure.core.codecs.fieldOf
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

public expect object ItemStackByteCodec : ByteCodec<ItemStack>


