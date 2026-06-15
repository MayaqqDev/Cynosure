package dev.mayaqq.cynosure.mixin;

import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.entity.player.PlayerRespawnEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(method = "respawn", at = @At("TAIL"))
    private void afterRespawn(ServerPlayer oldPlayer, boolean alive, CallbackInfoReturnable<ServerPlayer> cir) {
        MainBus.INSTANCE.post(new PlayerRespawnEvent(cir.getReturnValue(), oldPlayer, alive));
    }
}
