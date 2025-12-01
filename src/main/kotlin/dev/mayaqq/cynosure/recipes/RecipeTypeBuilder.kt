package dev.mayaqq.cynosure.recipes

import com.mojang.serialization.Codec
import com.teamresourceful.bytecodecs.base.ByteCodec
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import uwu.serenity.kritter.api.Registrar
import uwu.serenity.kritter.api.builders.GenericBuilder
import uwu.serenity.kritter.api.entry
import uwu.serenity.kritter.api.entry.RegistryEntry
import kotlin.properties.PropertyDelegateProvider

public fun <T : Recipe<*>> Registrar<RecipeType<*>>.recipeType(): PropertyDelegateProvider<Any?, RegistryEntry<RecipeType<T>>> = PropertyDelegateProvider { thisRef, property ->
    this.recipeType(property.name)
}

public inline fun <T : Recipe<*>> Registrar<RecipeType<*>>.recipeType(name: String, builder: GenericBuilder<RecipeType<*>, RecipeType<T>>.() -> Unit = {}): RegistryEntry<RecipeType<T>> =
    entry<_, RecipeType<T>>(name, ::SimpleRecipeType).apply(builder).register()

public inline fun <T : Recipe<*>> Registrar<RecipeSerializer<*>>.codecSerializer(
    name: String,
    noinline codec: (ResourceLocation) -> Codec<T>,
    noinline networkCodec: (ResourceLocation) -> ByteCodec<T>,
    builder: GenericBuilder<RecipeSerializer<*>, CodecRecipeSerializer<T>>.() -> Unit
): RegistryEntry<CodecRecipeSerializer<T>> = entry(name, { CodecRecipeSerializer(codec, networkCodec) }).apply(builder).register()

public class SimpleRecipeType<T : Recipe<*>> : RecipeType<T> {
    override fun toString(): String = "[${BuiltInRegistries.RECIPE_TYPE.getKey(this)}]"
}