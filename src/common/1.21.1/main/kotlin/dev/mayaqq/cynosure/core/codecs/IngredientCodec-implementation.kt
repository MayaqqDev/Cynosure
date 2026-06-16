package dev.mayaqq.cynosure.core.codecs

import com.mojang.serialization.Codec
import net.minecraft.world.item.crafting.Ingredient

//TODO: is this error real or not
public actual object IngredientCodec : Codec<Ingredient> by Ingredient.CODEC