package dev.mayaqq.cynosure.fabric.mixin;

import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.server.ServerChatEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @ModifyArg(
            method = "lambda$handleChat$10",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/ChatDecorator;decorate(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/Component;)Ljava/util/concurrent/CompletableFuture;"
            ),
            index = 1
    )
    private Component onChatMessage(Component message) {
        var event = new ServerChatEvent(player, message, getRawText(message));
        MainBus.INSTANCE.post(event);
        if (event.isCancelled()) return null;
        return event.getMessage();
    }

    @Unique
    private static String getRawText(Component message) {
        ComponentContents contents = message.getContents();
        String text;
        if (contents instanceof LiteralContents literalContents) {
            text = literalContents.text();
        } else {
            text = "";
        }

        return text;
    }
}
