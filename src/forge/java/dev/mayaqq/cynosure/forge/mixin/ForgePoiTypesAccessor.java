package dev.mayaqq.cynosure.forge.mixin;

import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(PoiTypes.class)
public interface ForgePoiTypesAccessor {
    @Accessor("TYPE_BY_STATE")
    @Mutable
    static void cynosure$setBlockStates(Map<BlockState, PoiType> map) {
        throw new UnsupportedOperationException();
    }

    @Accessor("TYPE_BY_STATE")
    static Map<BlockState, PoiType> cynosure$getBlockStates() {
        throw new UnsupportedOperationException();
    }
}
