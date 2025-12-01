package dev.mayaqq.cynosure.mixin;

import dev.mayaqq.cynosure.level.BlockUpdateListener;
import dev.mayaqq.cynosure.level.UpdateListenerSet;
import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerLevel.class)
public class ServerLevelMixin extends LevelMixin {

    @Unique
    private Long2ReferenceMap<BlockUpdateListener> cynosure$updateListeners;

    @Override
    public void cynosure_addUpdateListener(BlockUpdateListener listener) {
        if(cynosure$updateListeners == null) cynosure$updateListeners = new Long2ReferenceOpenHashMap<>();
        for(BlockPos pos : listener.getListenedPositions()) {
            long longPos = pos.asLong();
            BlockUpdateListener current = cynosure$updateListeners.get(longPos);
            if (current instanceof UpdateListenerSet set) {
                set.add$cynosure(listener);
            } else if (current != null) {
                var set = new UpdateListenerSet(current, listener);
                cynosure$updateListeners.put(longPos, set);
            } else {
                cynosure$updateListeners.put(longPos, listener);
            }
        }
    }

    @Override
    public void cynosure_handleBlockUpdate(BlockPos pos, BlockState state) {
        if(cynosure$updateListeners == null) return;
        long longPos = pos.asLong();
        BlockUpdateListener listener = cynosure$updateListeners.get(longPos);
        if (listener != null) {
            if (listener.shouldRemove((ServerLevel) (Object) this)) cynosure$updateListeners.remove(longPos);
            else listener.onBlockUpdate((ServerLevel) (Object) this, pos, state);
        }
    }
}
