package dev.mayaqq.cynosure.fabric.mixin;

import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.world.LevelEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(
        method = "save",
        at = @At("HEAD")
    )
    private void onSave(ProgressListener progressListener, boolean bl, boolean bl2, CallbackInfo ci) {
        MainBus.INSTANCE.post(new LevelEvent.Save((ServerLevel) (Object) this), "server", null);
    }
}
