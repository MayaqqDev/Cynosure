package dev.mayaqq.cynosure.mixin.client;

import dev.mayaqq.cynosure.injection.client.ILanguageManager;
import dev.mayaqq.cynosure.injection.client.IMinecraft;
import dev.mayaqq.cynosure.music.MusicApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;

@Mixin(Minecraft.class)
public class MinecraftMixin implements IMinecraft {
    @Shadow public LocalPlayer player;

    @Shadow @Final private MusicManager musicManager;

    @Shadow @Final private LanguageManager languageManager;

    @Inject(
            method = "getSituationalMusic()Lnet/minecraft/sounds/Music;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;blockPosition()Lnet/minecraft/core/BlockPos;",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void onGetSituationalMusic(CallbackInfoReturnable<Music> cir) {
        Holder<Biome> biome = player.level().getBiome(this.player.blockPosition());
        MusicApi.musics.forEach((music, supplier) -> {
            if (supplier.invoke(player, musicManager, biome)) {
                cir.setReturnValue(music);
            }
        });
    }

    @Override
    public @NotNull Locale cynosure$javaLocale() {
        return ((ILanguageManager) languageManager).cynosure$javaLocale();
    }
}
