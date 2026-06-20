package dev.mayaqq.cynosure.core.codecs.item

import com.mojang.serialization.Codec
import com.mojang.serialization.Decoder
import net.minecraft.world.item.ItemStack

public actual object ItemStackCodec : Codec<ItemStack> by ItemStack.CODEC