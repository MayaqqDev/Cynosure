package dev.mayaqq.cynosure.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.mayaqq.cynosure.client.splash.CynosureSplashRenderer;
import dev.mayaqq.cynosure.client.splash.data.CynosureSplashLoader;
import dev.mayaqq.cynosure.mixin.accessor.SplashRendererAccessor;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(SplashManager.class)
public class SplashManagerMixin {

    @Shadow @Final private static RandomSource RANDOM;

    @ModifyReturnValue(
            method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Ljava/util/List;",
            at = @At("RETURN")
    )
    private List<String> modifySplashList(List<String> list) {
        if (true /*TODO: Maybe a config check or something?*/) {
            for (int i = 0; i < CynosureSplashLoader.INSTANCE.getAmount(); i++) list.add("cynosure:splashes");
        }
        return list;
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
