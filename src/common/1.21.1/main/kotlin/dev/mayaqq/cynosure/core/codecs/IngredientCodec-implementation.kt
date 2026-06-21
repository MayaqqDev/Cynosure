package dev.mayaqq.cynosure.core.codecs

import com.mojang.serialization.Codec
import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.core.bytecodecs.FriendlyByteCodec
import dev.mayaqq.cynosure.core.bytecodecs.toByteCodec
import net.minecraft.world.item.crafting.Ingredient

public actual object IngredientCodec : Codec<Ingredient> by Ingredient.CODEC {
    actual val NETWORK: ByteCodec<Ingredient> = Ingredient.CONTENTS_STREAM_CODEC.toByteCodec()
}