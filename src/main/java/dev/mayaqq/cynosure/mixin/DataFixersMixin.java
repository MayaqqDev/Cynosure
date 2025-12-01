package dev.mayaqq.cynosure.mixin;

import com.mojang.datafixers.DataFixerBuilder;
import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.datafix.DataFixerRegistrationEvent;
import net.minecraft.util.datafix.DataFixers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataFixers.class)
public class DataFixersMixin {

    @Inject(
        method = "addFixers",
        at = @At("RETURN")
    )
    private static void onCreateDatafixers(DataFixerBuilder $$0, CallbackInfo ci) {
        MainBus.INSTANCE.post(new DataFixerRegistrationEvent($$0), null, null);
    }

}
