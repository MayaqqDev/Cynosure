package dev.mayaqq.cynosure.mixin;

import dev.mayaqq.cynosure.injection.ILevel;
import dev.mayaqq.cynosure.level.BlockUpdateListener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public abstract class LevelMixin implements ILevel {

    @Override
    public void cynosure_addUpdateListener(BlockUpdateListener listener) {
        // Only does something on server
    }

    @Override
    public void cynosure_handleBlockUpdate(BlockPos pos, BlockState state) {
        // Only does something on server [see ServerLevelMixin]
    }
}
