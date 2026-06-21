package dev.mayaqq.cynosure.fabric.v1211.mixin.client;

import dev.mayaqq.cynosure.injection.client.ILanguageManager;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;
import java.util.function.Consumer;

// Mixin check: should work

@Mixin(LanguageManager.class)
public class LanguageManagerMixin implements ILanguageManager {

    @Unique
    public Locale javaLocale;

    @Inject(
            method = "<init>",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/resources/language/LanguageManager;currentCode:Ljava/lang/String;")
    )
    private void javaLocaleAssign(String currentCode, Consumer<ClientLanguage> reloadFallback, CallbackInfo ci) {
        final String[] langSplit = currentCode.split("_", 2);
        this.javaLocale = langSplit.length == 1 ? new Locale(langSplit[0]) : new Locale(langSplit[0], langSplit[1]);
    }

    @Override
    public @NotNull Locale cynosure$javaLocale() {
        return javaLocale;
    }
}