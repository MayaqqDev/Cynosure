package dev.mayaqq.cynosure.mixin.client;

import dev.mayaqq.cynosure.client.events.render.GameRenderEvent;
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
        at = @At("RETURN")
    )
    private void onResizeRenderer(int width, int height, CallbackInfo ci) {
        MainBus.INSTANCE.post(new ResizeRendererEvent(width, height), null, null);
    }

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void onRenderStart(float partialTick, long nanos, boolean bl, CallbackInfo ci) {
        MainBus.INSTANCE.post(new GameRenderEvent(partialTick, nanos), null, null);
    }
}
