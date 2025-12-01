package dev.mayaqq.cynosure.mixin;

import dev.mayaqq.cynosure.injection.ILevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChunkHolder.class)
@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
public class ChunkHolderMixin {

    @Inject(
        method = "broadcastBlockEntityIfNeeded",
        at = @At("HEAD")
    )
    public void handleUpdate(List list, Level level, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        if(!level.isClientSide) ((ILevel) level).cynosure_handleBlockUpdate(blockPos, blockState);
    }
}
