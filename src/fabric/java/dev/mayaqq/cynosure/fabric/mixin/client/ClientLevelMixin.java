package dev.mayaqq.cynosure.fabric.mixin.client;

import dev.mayaqq.cynosure.events.ExtraEventHandling;
import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.world.LevelEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    public void onLoadClientLevel(ClientPacketListener clientPacketListener, ClientLevel.ClientLevelData clientLevelData, ResourceKey resourceKey, Holder holder, int i, int j, Supplier supplier, LevelRenderer levelRenderer, boolean bl, long l, CallbackInfo ci) {
        MainBus.INSTANCE.post(new LevelEvent.Load((Level) (Object) this), null, null);
    }

    @Inject(
        method = "addEntity",
        at = @At("HEAD"),
        cancellable = true
    )
    public void runEntityCreationEvents(int i, Entity entity, CallbackInfo ci) {
        if (!ExtraEventHandling.onCreateEntity((ClientLevel) (Object) this, entity)) ci.cancel();
    }
}
