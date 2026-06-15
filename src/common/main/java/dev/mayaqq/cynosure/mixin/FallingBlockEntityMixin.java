package dev.mayaqq.cynosure.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.block.FallingBlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {
    @Shadow private BlockState blockState;

    public FallingBlockEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
        method = "fall",
        at = @At("RETURN")
    )
    private static void onFall(Level $$0, BlockPos $$1, BlockState $$2, CallbackInfoReturnable<FallingBlockEntity> cir) {
        FallingBlockEntity entity = cir.getReturnValue();
        MainBus.INSTANCE.post(new FallingBlockEvent.Fall($$0, $$2, entity, $$1));
    }

    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    private void onBeginFallingBlockTick(CallbackInfo ci) {
        FallingBlockEntity self = (FallingBlockEntity) (Object) this;
        MainBus.INSTANCE.post(new FallingBlockEvent.Tick(self.level(), blockState, self));

    }

    @ModifyArg(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
        ),
        index = 1
    )
    private BlockState onFallingBlockLand(BlockPos $$0, BlockState $$1, int $$2, @Local(ordinal = 0) BlockState stateBelow, @Local BlockPos pos) {
        FallingBlockEntity self = (FallingBlockEntity) (Object) this;
        FallingBlockEvent.Land event = new FallingBlockEvent.Land(level(), $$1, self, pos, stateBelow);
        MainBus.INSTANCE.post(event);
        BlockState placedState = event.getPlacedState();
        return (placedState != null) ? placedState : $$1;
    }

}
