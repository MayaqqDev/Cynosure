package dev.mayaqq.cynosure.mixin;

import dev.mayaqq.cynosure.events.api.EventBus;
import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.registry.BedEntityBlockRegisterEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.List;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {
    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/BlockEntityType$Builder;of(Lnet/minecraft/world/level/block/entity/BlockEntityType$BlockEntitySupplier;[Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/entity/BlockEntityType$Builder;"
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/world/level/block/entity/BlockEntityType;SHULKER_BOX:Lnet/minecraft/world/level/block/entity/BlockEntityType;",
                            opcode = Opcodes.PUTSTATIC
                    ),
                    to = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/world/level/block/entity/BlockEntityType;BED:Lnet/minecraft/world/level/block/entity/BlockEntityType;",
                            opcode = Opcodes.PUTSTATIC
                    )
            ),
            index = 1
    )
    private static Block[] modifyBedArgs(Block[] blocks) {
        var event = new BedEntityBlockRegisterEvent();
        MainBus.INSTANCE.post(event);
        var newBlocks = event.getBedBlocks();
        newBlocks.addAll(List.of(blocks));
        return newBlocks.toArray(Block[]::new);
    }
}
