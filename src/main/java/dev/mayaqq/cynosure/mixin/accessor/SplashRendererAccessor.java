package dev.mayaqq.cynosure.mixin.accessor;

import net.minecraft.client.gui.components.SplashRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SplashRenderer.class)
public interface SplashRendererAccessor {
    @Accessor
    String getSplash();
}
