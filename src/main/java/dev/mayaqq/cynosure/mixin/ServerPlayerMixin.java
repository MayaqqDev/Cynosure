package dev.mayaqq.cynosure.mixin;

import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.entity.player.PlayerRestoreEvent;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "restoreFrom", at = @At("HEAD"))
    private void restoreFrom(ServerPlayer player, boolean alive, CallbackInfo ci) {
        MainBus.INSTANCE.post(new PlayerRestoreEvent(player, alive), null, null);
    }
}
