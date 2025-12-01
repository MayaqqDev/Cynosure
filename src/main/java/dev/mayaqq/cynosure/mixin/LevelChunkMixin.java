package dev.mayaqq.cynosure.mixin;

import dev.mayaqq.cynosure.injection.ILevel;
import dev.mayaqq.cynosure.level.BlockUpdateListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {

    @Shadow @Final
    Level level;

    @Inject(
        method = "addAndRegisterBlockEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/chunk/LevelChunk;addGameEventListener(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/server/level/ServerLevel;)V"
        )
    )
    private void registerUpdateListener(BlockEntity blockEntity, CallbackInfo ci) {
        if (blockEntity instanceof BlockUpdateListener listener) {
            ((ILevel) level).cynosure_addUpdateListener(listener);
        }
    }
}
