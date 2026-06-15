package dev.mayaqq.cynosure.fabric.mixin;

import dev.mayaqq.cynosure.events.ExtraEventHandling;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Shadow @Nullable private Entity vehicle;

    @Inject(
            method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;canRide(Lnet/minecraft/world/entity/Entity;)Z"
            ),
            cancellable = true
    )
    private void startRiding(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> cir) {
        if (!ExtraEventHandling.canMountEntity((Entity) (Object) this, vehicle, true)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "removeVehicle",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/Entity;vehicle:Lnet/minecraft/world/entity/Entity;",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void removeVehicle(CallbackInfo ci) {
        ExtraEventHandling.canMountEntity((Entity) (Object) this, vehicle, false);
    }
}
