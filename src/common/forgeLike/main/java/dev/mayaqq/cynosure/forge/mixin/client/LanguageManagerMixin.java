package dev.mayaqq.cynosure.forge.mixin.client;

import dev.mayaqq.cynosure.injection.client.ILanguageManager;
import net.minecraft.client.resources.language.LanguageManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Locale;

@Mixin(LanguageManager.class)
public class LanguageManagerMixin implements ILanguageManager {
    @Shadow private Locale javaLocale;

    @Override
    public @NotNull Locale cynosure$javaLocale() {
        return this.javaLocale;
    }
}
