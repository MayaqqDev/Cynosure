package dev.mayaqq.cynosure.recipes

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.core.bytecodecs.toStreamCodec
import dev.mayaqq.cynosure.modId
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer

public actual class CodecRecipeSerializer<T : Recipe<*>> actual constructor(
    private val codec: (ResourceLocation) -> Codec<T> ,
    private val networkCodec: (ResourceLocation) -> ByteCodec<T>
) : RecipeSerializer<T> {
    override fun codec(): MapCodec<T> = MapCodec.assumeMapUnsafe(codec.invoke(modId("empty")))

    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, T> = networkCodec.invoke(modId("empty")).toStreamCodec()
}