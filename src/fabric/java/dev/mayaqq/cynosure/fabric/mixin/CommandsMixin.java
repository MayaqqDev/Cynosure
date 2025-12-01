package dev.mayaqq.cynosure.fabric.mixin;

import com.google.common.base.Throwables;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.command.CommandExecuteEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Commands.class)
public class CommandsMixin {

    @WrapOperation(
        method = "performCommand",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/brigadier/CommandDispatcher;execute(Lcom/mojang/brigadier/ParseResults;)I"
        )
    )
    private int wrapExecute(CommandDispatcher<CommandSourceStack> instance, ParseResults<CommandSourceStack> results, Operation<Integer> original) {
        CommandExecuteEvent event = new CommandExecuteEvent(results, null);
        if (MainBus.INSTANCE.post(event, null, null)) {
            if (event.getException() != null) {
                Throwables.throwIfUnchecked(event.getException());
            }
            return 1;
        }
        return original.call(instance, event.getParseResults());
    }
}
