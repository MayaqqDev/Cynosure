package dev.mayaqq.cynosure.v1211.mixin.client;

import dev.mayaqq.cynosure.client.events.render.GameRenderEvent;
import dev.mayaqq.cynosure.events.api.MainBus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Mixin check: should work, not sure about the delta nanos

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void onRenderStart(DeltaTracker delta, boolean bl, CallbackInfo ci) {
        MainBus.INSTANCE.post(new GameRenderEvent(delta.getGameTimeDeltaTicks(), (long) delta.getRealtimeDeltaTicks()));
    }
}
