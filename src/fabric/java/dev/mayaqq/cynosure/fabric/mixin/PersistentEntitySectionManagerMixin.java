package dev.mayaqq.cynosure.fabric.mixin;

import dev.mayaqq.cynosure.events.ExtraEventHandling;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentEntitySectionManager.class)
public class PersistentEntitySectionManagerMixin {

    @Inject(
        method = "addEntity",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onCreateEntity(EntityAccess entityAccess, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (entityAccess instanceof Entity entity) {
            if (!ExtraEventHandling.onCreateEntity(entity.level(), entity)) cir.setReturnValue(false);
        }
    }
}
