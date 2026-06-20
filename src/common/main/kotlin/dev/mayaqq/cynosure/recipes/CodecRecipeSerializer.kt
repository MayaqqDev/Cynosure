package dev.mayaqq.cynosure.recipes

import com.mojang.serialization.Codec
import com.teamresourceful.bytecodecs.base.ByteCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer

public expect class CodecRecipeSerializer<T : Recipe<*>>(
    codec: (ResourceLocation) -> Codec<T>,
    networkCodec: (ResourceLocation) -> ByteCodec<T>,
) : RecipeSerializer<T>