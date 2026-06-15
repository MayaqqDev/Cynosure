package dev.mayaqq.cynosure.injection

import net.minecraft.client.RecipeBookCategories
import net.minecraft.world.inventory.RecipeBookType

public interface IRecipeBookTypes {
    public fun cynosure_create(name: String): RecipeBookType
}

public interface IRecipeBookCategories {
    public fun cynosure_create(name: String): RecipeBookCategories
}