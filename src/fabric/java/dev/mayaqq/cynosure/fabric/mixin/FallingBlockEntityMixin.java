package dev.mayaqq.cynosure.fabric.mixin;

import net.minecraft.world.entity.item.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {

//    @Inject(
//        method = "tick",
//        at = @At("HEAD")
//    )
//    private void tickFallingBlockEntity(CallbackInfo ci) {
//
//        if (!MainBus.INSTANCE.post(FallingBlockEvent.Tick()))
//    }
}
