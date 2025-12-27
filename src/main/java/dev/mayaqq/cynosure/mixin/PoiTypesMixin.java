package dev.mayaqq.cynosure.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mayaqq.cynosure.blocks.poi.PoiHelpers;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;


@Mixin(PoiTypes.class)
public class PoiTypesMixin {
    @ModifyReturnValue(
            method = "forState",
            at = @At("RETURN")
    )
    private static Optional<Holder<PoiType>> addCynosurePoiInjectsForStates(Optional<Holder<PoiType>> original, @Local(argsOnly = true) BlockState state) {
        if (original.isEmpty() && PoiHelpers.ADDITIONAL_POI_STATE_PAIRS.containsKey(state)) {
            return Optional.ofNullable(PoiHelpers.ADDITIONAL_POI_STATE_PAIRS.get(state));
        }
        return original;
    }

    @ModifyReturnValue(
            method = "hasPoi",
            at = @At("RETURN")
    )
    private static boolean addCynosurePoiInjectsForBlocks(boolean original, @Local(argsOnly = true) BlockState state) {
        return PoiHelpers.ADDITIONAL_POI_STATE_PAIRS.containsKey(state) || original;
    }
}
