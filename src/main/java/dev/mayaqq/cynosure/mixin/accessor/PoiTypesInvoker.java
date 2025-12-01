package dev.mayaqq.cynosure.mixin.accessor;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(PoiTypes.class)
public interface PoiTypesInvoker {
    @Invoker("registerBlockStates")
    static void registerBlockStates(Holder<PoiType> poiType, Set<BlockState> blockStates) {
        throw new AssertionError();
    }
}
