package dev.mayaqq.cynosure.injection.client

import net.minecraft.client.Minecraft
import java.util.Locale

public interface IMinecraft {
    public fun `cynosure$javaLocale`(): Locale
}

public val Minecraft.javaLocale: Locale
    get() = (this as IMinecraft).`cynosure$javaLocale`()