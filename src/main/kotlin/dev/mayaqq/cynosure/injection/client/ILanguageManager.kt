package dev.mayaqq.cynosure.injection.client

import net.minecraft.client.resources.language.LanguageManager
import java.util.Locale

public interface ILanguageManager {
    public fun `cynosure$javaLocale`(): Locale
}

public val LanguageManager.javaLocale: Locale
    get() = (this as ILanguageManager).`cynosure$javaLocale`()