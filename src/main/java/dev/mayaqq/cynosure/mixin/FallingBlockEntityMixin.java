package dev.mayaqq.cynosure.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.block.FallingBlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {
    @Shadow private BlockState blockState;

    @Inject(
        method = "fall",
        at = @At("RETURN")
    )
    private static void onFall(Level $$0, BlockPos $$1, BlockState $$2, CallbackInfoReturnable<FallingBlockEntity> cir) {
        FallingBlockEntity entity = cir.getReturnValue();
        MainBus.INSTANCE.post(new FallingBlockEvent.Fall($$0, $$2, entity, $$1), null, null);
    }

    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    private void onBeginFallingBlockTick(CallbackInfo ci) {
        FallingBlockEntity self = (FallingBlockEntity) (Object) this;
        MainBus.INSTANCE.post(new FallingBlockEvent.Tick(self.level(), blockState, self), null, null);

    }

    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
        )
    )
    private boolean onFallingBlockLand(Level instance, BlockPos $$0, BlockState $$1, int $$2, Operation<Boolean> original, @Local BlockState stateBelow, @Local BlockPos pos) {
        FallingBlockEntity self = (FallingBlockEntity) (Object) this;
        FallingBlockEvent.Land event = new FallingBlockEvent.Land(instance, $$1, self, pos, stateBelow);
        MainBus.INSTANCE.post(event, null, null);
        BlockState placedState = event.getPlacedState();
        if (placedState != null) {
            return original.call(instance, $$0, placedState, $$2);
        }
        return original.call(instance, $$0, $$1, $$2);
    }

}
