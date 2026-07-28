package dev.mayaqq.cynosure.mixin;

import com.google.common.collect.ImmutableSet;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.registry.VanillaBlockEntityRegistrationEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

@Mixin(BlockEntityType.Builder.class)
public class BlockEntityTypeBuilderMixin {
    @ModifyArg(
            method = "of",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/BlockEntityType$Builder;<init>(Lnet/minecraft/world/level/block/entity/BlockEntityType$BlockEntitySupplier;Ljava/util/Set;)V"
            ),
            index = 1
    )
    private static Set makeNotMutable(Set original, @Local Block[] blocks) {
        return new LinkedHashSet<>(Arrays.stream(blocks).toList());
    }
}