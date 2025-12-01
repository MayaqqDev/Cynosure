package dev.mayaqq.cynosure.fabric.mixin;

import dev.mayaqq.cynosure.injection.IRecipeBookTypes;
import net.minecraft.world.inventory.RecipeBookType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Arrays;

@Mixin(RecipeBookType.class)
public class RecipeBookTypeMixin implements IRecipeBookTypes {

    @Shadow @Final @Mutable
    private static RecipeBookType[] $VALUES;

    @Invoker("<init>")
    static RecipeBookType createRecipeBookType(String id, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull RecipeBookType cynosure_create(@NotNull String name) {
        RecipeBookType type = createRecipeBookType(name, $VALUES.length);
        RecipeBookType[] newConstants = Arrays.copyOf($VALUES, $VALUES.length + 1);
        newConstants[$VALUES.length] = type;
        $VALUES = newConstants;
        return type;
    }
}
