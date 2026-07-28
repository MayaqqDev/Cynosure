package dev.mayaqq.cynosure.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.registry.VanillaBlockEntityRegistrationEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.Set;

@Mixin(BlockEntityType.Builder.class)
public class BlockEntityTypeBuilderMixin<T extends BlockEntity> {
    @ModifyVariable(
            method = "<init>",
            at = @At(
                    value = "HEAD"
            ),
            ordinal = 1,
            argsOnly = true
    )
    private Set<Block> modifyBedArgs(Set<Block> validBlocks, @Local BlockEntityType.BlockEntitySupplier<? extends T> supplier) {
        var event = new VanillaBlockEntityRegistrationEvent(supplier.getClass().componentType());
        MainBus.INSTANCE.post(event);
        var newBlocks = event.getBlocks();
        newBlocks.addAll(validBlocks);
        return newBlocks;
    }
}
