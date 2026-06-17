package dev.mayaqq.cynosure.mixin.client;

import dev.mayaqq.cynosure.client.events.render.ResizeRendererEvent;
import dev.mayaqq.cynosure.events.api.MainBus;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(
        method = "resize",
        at = @At("TAIL")
    )
    private void onResizeRenderer(int width, int height, CallbackInfo ci) {
        MainBus.INSTANCE.post(new ResizeRendererEvent(width, height));
    }
}
