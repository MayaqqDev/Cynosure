package dev.mayaqq.cynosure.core.codecs

import com.mojang.serialization.Codec
import com.teamresourceful.bytecodecs.base.ByteCodec
import net.minecraft.world.item.crafting.Ingredient

public expect object IngredientCodec : Codec<Ingredient> {
    public val NETWORK: ByteCodec<Ingredient>
}