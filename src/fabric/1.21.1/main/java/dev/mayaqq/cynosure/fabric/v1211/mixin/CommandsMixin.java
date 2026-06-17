package dev.mayaqq.cynosure.fabric.v1211.mixin;

import com.google.common.base.Throwables;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ContextChain;
import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.command.CommandExecuteEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//TODO: check if this works

@Mixin(Commands.class)
public class CommandsMixin {

    @WrapOperation(
        method = "performCommand",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/commands/Commands;finishParsing(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;Lnet/minecraft/commands/CommandSourceStack;)Lcom/mojang/brigadier/context/ContextChain;"
        )
    )
    private ContextChain wrapExecute(ParseResults results, String command, CommandSourceStack source, Operation<ContextChain> original) {
        CommandExecuteEvent event = new CommandExecuteEvent(results, null);
        if (MainBus.INSTANCE.post(event)) {
            if (event.getException() != null)
                Throwables.throwIfUnchecked(event.getException());
            return null;
        }
        return original.call(event.getParseResults(), command, source);
    }
}
