package dev.mayaqq.cynosure.fabric.v1211.mixin.client;

import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.world.LevelEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow @Nullable
    public ClientLevel level;

    @Inject(
        method = "setLevel",
        at = @At("HEAD")
    )
    private void onChangeLevel(ClientLevel level, ReceivingLevelScreen.Reason reason, CallbackInfo ci) {
        if (level != null) MainBus.INSTANCE.post(new LevelEvent.Unload(level));
    }

    @Inject(
        method = "clearClientLevel",
        at = @At("HEAD")
    )
    private void onClearLevel(CallbackInfo ci) {
        if (level != null) MainBus.INSTANCE.post(new LevelEvent.Unload(level));
    }

}
