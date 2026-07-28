package dev.mayaqq.cynosure.mixin;

import dev.mayaqq.cynosure.Cynosure;
import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.registry.VanillaBlockEntityRegistrationEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {
    @Inject(
            method = "register",
            at = @At("HEAD")
    )
    private static <T extends BlockEntity> void modifyBedArgs(
            String id,
            BlockEntityType.Builder<T> builder,
            CallbackInfoReturnable<BlockEntityType<T>> cir
    ) {
        var event = new VanillaBlockEntityRegistrationEvent(id);
        MainBus.INSTANCE.post(event);
        var newBlocks = event.getBlocks();
        builder.validBlocks.addAll(newBlocks);
    }
}
