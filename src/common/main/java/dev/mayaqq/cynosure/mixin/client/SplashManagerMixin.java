package dev.mayaqq.cynosure.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mayaqq.cynosure.Cynosure;
import dev.mayaqq.cynosure.client.splash.CynosureSplashRenderer;
import dev.mayaqq.cynosure.client.splash.data.CynosureSplashLoader;
import dev.mayaqq.cynosure.mixin.accessor.SplashRendererAccessor;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

// Mixin check: should work

@Mixin(SplashManager.class)
public class SplashManagerMixin {

    @Shadow @Final private static RandomSource RANDOM;

    @Shadow
    @Final
    private List<String> splashes;
    @Unique
     private boolean estrogen$actuallyInitialized = false;

    @Inject(
            method = "getSplash",
            at = @At("HEAD")
    )
    private void addCynosure(CallbackInfoReturnable<SplashRenderer> cir) {
        if (!estrogen$actuallyInitialized) {
            for (int i = 0; i < CynosureSplashLoader.INSTANCE.getSplashes().size(); i++) {
                splashes.add("cynosure:splashes");
            }
            estrogen$actuallyInitialized = true;
        }
    }

    @ModifyReturnValue(
            method = "getSplash",
            at = @At("RETURN")
    )
    private SplashRenderer modifySplash(SplashRenderer original) {
        if (original instanceof SplashRendererAccessor accessor && "cynosure:splashes".equals(accessor.getSplash())) {
            return new CynosureSplashRenderer(RANDOM);
        }
        return original;
    }
}
