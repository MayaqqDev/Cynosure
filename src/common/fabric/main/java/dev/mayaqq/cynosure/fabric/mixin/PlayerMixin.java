package dev.mayaqq.cynosure.fabric.mixin;

import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.entity.player.PlayerTickEvent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void playerTickStart(CallbackInfo ci) {
        PlayerTickEvent.Begin event = new PlayerTickEvent.Begin((Player) (Object) this);
        MainBus.INSTANCE.post(event);
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void playerTickEnd(CallbackInfo ci) {
        PlayerTickEvent.End event = new PlayerTickEvent.End((Player) (Object) this);
        MainBus.INSTANCE.post(event);
    }
}
