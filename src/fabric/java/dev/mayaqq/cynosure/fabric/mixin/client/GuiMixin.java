package dev.mayaqq.cynosure.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.mayaqq.cynosure.client.events.render.BeginHudRenderEvent;
import dev.mayaqq.cynosure.client.render.gui.HudOverlay;
import dev.mayaqq.cynosure.client.render.gui.HudOverlayRegistry;
import dev.mayaqq.cynosure.client.render.gui.VanillaHud;
import dev.mayaqq.cynosure.events.api.MainBus;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow protected abstract Player getCameraPlayer();

    @Unique
    private Map<VanillaHud, List<HudOverlay>> hudOverlays = null;

    @Unique
    private void renderPhaseOverlays(VanillaHud phase, GuiGraphics guiGraphics, float partialTick) {
        boolean blendEnabled = GlStateManager.BLEND.mode.enabled;
        boolean depthEnabled = GlStateManager.DEPTH.mode.enabled;
        for(HudOverlay overlay : hudOverlays.get(phase)) {
            RenderSystem.enableBlend();
            overlay.render((Gui) (Object) this, guiGraphics, partialTick);
        }
        // Restore render state
        if (blendEnabled) RenderSystem.enableBlend(); else RenderSystem.disableBlend();
        if (depthEnabled) RenderSystem.enableDepthTest(); else RenderSystem.disableDepthTest();
    }

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void onGuiInit(CallbackInfo ci) {
        hudOverlays = HudOverlayRegistry.INSTANCE.getSorted();
    }

    @Inject(
        method = "render",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onBeginRenderHud(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        var event = new BeginHudRenderEvent((Gui) (Object) this, guiGraphics, partialTick);
        if(MainBus.INSTANCE.post(event, null, null)) ci.cancel();
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;useFancyGraphics()Z"
        ),
        slice = @Slice(
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/Gui;renderVignette(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/Entity;)V"
            )
        )
    )
    private void beforeRenderVignette(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.VIGNETTE, guiGraphics, partialTick);
    }

    @ModifyExpressionValue(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"
        )
    )
    private boolean renderLayersIfNotFirstPerson(boolean original, @Local(argsOnly = true) GuiGraphics graphics, @Local(argsOnly = true) float partialTick) {
        if(!original) {
            renderPhaseOverlays(VanillaHud.SPYGLASS, graphics, partialTick);
            renderPhaseOverlays(VanillaHud.HELMET, graphics, partialTick);
        }
        return original;
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isScoping()Z"
        )
    )
    private void beforeRenderSpyGlass(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.SPYGLASS, guiGraphics, partialTick);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"
            ),
            to = @At(
                value = "FIELD",
                target = "Lnet/minecraft/client/gui/Gui;PUMPKIN_BLUR_LOCATION:Lnet/minecraft/resources/ResourceLocation;",
                opcode = Opcodes.GETSTATIC
            )
        )
    )
    private void beforePumpkinOverlay(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.HELMET, guiGraphics, partialTick);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getTicksFrozen()I"
        )
    )
    private void beforeFreezingOverlay(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.FROSTBITE, guiGraphics, partialTick);
    }

    @Inject(
        method = "render",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/player/LocalPlayer;getTicksFrozen()I"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/Gui;renderPortalOverlay(Lnet/minecraft/client/gui/GuiGraphics;F)V"
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/Mth;lerp(FFF)F",
            shift = At.Shift.AFTER
        )
    )
    private void beforePortalOverlay(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.PORTAL, guiGraphics, partialTick);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;getPlayerMode()Lnet/minecraft/world/level/GameType;"
        ),
        slice = @Slice(
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/Gui;renderHotbar(FLnet/minecraft/client/gui/GuiGraphics;)V"
            )
        )
    )
    private void beforeHotbar(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.HOTBAR, guiGraphics, partialTick);
    }

    @ModifyExpressionValue(
        method = "render",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/Options;hideGui:Z",
            opcode = Opcodes.GETFIELD
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/Gui;renderHotbar(FLnet/minecraft/client/gui/GuiGraphics;)V"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;)V"
            )
        )
    )
    private boolean renderLayersIfGuiHidden0(boolean original, @Local(argsOnly = true) GuiGraphics graphics, @Local(argsOnly = true) float partialTick) {
        if(!original) {
            renderPhaseOverlays(VanillaHud.CROSSHAIR, graphics, partialTick);
            renderPhaseOverlays(VanillaHud.BOSS_BAR, graphics, partialTick);
            renderPhaseOverlays(VanillaHud.PLAYER_HEALTH, graphics, partialTick);
            renderPhaseOverlays(VanillaHud.FOOD_LEVEL, graphics, partialTick);
            renderPhaseOverlays(VanillaHud.MOUNT_HEALTH, graphics, partialTick);
            renderPhaseOverlays(VanillaHud.JUMP_BAR, graphics, partialTick);
            renderPhaseOverlays(VanillaHud.XP_BAR, graphics, partialTick);
            renderPhaseOverlays(VanillaHud.ITEM_NAME, graphics, partialTick);
        }
        return original;
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;)V"
        )
    )
    private void beforeRenderCrosshair(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.CROSSHAIR, guiGraphics, partialTick);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"
        )
    )
    private void beforeBossOverlay(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.BOSS_BAR, guiGraphics, partialTick);
    }

    @ModifyExpressionValue(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;canHurtPlayer()Z"
        )
    )
    private boolean ifHealthAndFoodHidden(boolean original, @Local(argsOnly = true) GuiGraphics graphics, @Local(argsOnly = true) float partialTick) {
        if (!original) {
            renderPhaseOverlays(VanillaHud.PLAYER_HEALTH, graphics, partialTick);
            renderPhaseOverlays(VanillaHud.FOOD_LEVEL, graphics, partialTick);
        }
        capturedPartialTick = partialTick;
        return original;
    }

    @Unique
    private float capturedPartialTick = 0f;

    @Inject(
        method = "renderPlayerHealth",
        at = @At("HEAD")
    )
    private void beforePlayerHealth(GuiGraphics guiGraphics, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.PLAYER_HEALTH, guiGraphics, capturedPartialTick);
        if(getCameraPlayer() == null) renderPhaseOverlays(VanillaHud.FOOD_LEVEL, guiGraphics, capturedPartialTick);
    }

    @Inject(
        method = "renderPlayerHealth",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I",
            shift = At.Shift.AFTER
        )
    )
    private void beforeFoodLevel(GuiGraphics guiGraphics, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.FOOD_LEVEL, guiGraphics, capturedPartialTick);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphics;)V")
    )
    private void beforeVehicleHealth(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.MOUNT_HEALTH, guiGraphics, partialTick);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;jumpableVehicle()Lnet/minecraft/world/entity/PlayerRideableJumping;"
        )
    )
    private void beforeJumpBar(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.JUMP_BAR, guiGraphics, partialTick);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasExperience()Z"
        )
    )
    private void beforeXpBar(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.XP_BAR, guiGraphics, partialTick);
    }

    @Inject(
        method = "render",
        at = {
            @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/Gui;renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;)V"
            ),
            @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/components/spectator/SpectatorGui;renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;)V"
            )
        }
    )
    private void beforeSpectatorTooltip(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.ITEM_NAME, guiGraphics, partialTick);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getSleepTimer()I",
            ordinal = 0
        )
    )
    private void beforeSleep(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.SLEEP_FADE, guiGraphics, partialTick);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderEffects(Lnet/minecraft/client/gui/GuiGraphics;)V"
        )
    )
    private void beforeEffects(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.EFFECTS, guiGraphics, partialTick);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/Options;renderDebug:Z"
        )
    )
    private void beforeDebug(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.DEBUG, guiGraphics, partialTick);
    }

    @ModifyExpressionValue(
        method = "render",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/Options;hideGui:Z"
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"
            )
        )
    )
    private boolean renderLayersIfGuiHidden1(boolean original, @Local(argsOnly = true) GuiGraphics graphics, @Local(argsOnly = true) float partialTick) {
        if(!original) {
            renderPhaseOverlays(VanillaHud.OVERLAY_MESSAGE, graphics, partialTick);
            renderPhaseOverlays(VanillaHud.TITLE_TEXT, graphics, partialTick);
            renderPhaseOverlays(VanillaHud.SUBTITLES, graphics, partialTick);
            renderPhaseOverlays(VanillaHud.SCOREBOARD, graphics, partialTick);
            renderPhaseOverlays(VanillaHud.CHAT, graphics, partialTick);
            renderPhaseOverlays(VanillaHud.PLAYER_LIST, graphics, partialTick);
        }
        return original;
    }

    @Inject(
        method = "render",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/Gui;overlayMessageString:Lnet/minecraft/network/chat/Component;",
            opcode = Opcodes.GETFIELD
        ),
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/minecraft/client/Options;hideGui:Z"
            ),
            to = @At(
                value = "INVOKE_STRING",
                target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V",
                args = "ldc=overlayMessage")
        )
    )
    private void beforeOverlayMessageString(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.OVERLAY_MESSAGE, guiGraphics, partialTick);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/Gui;title:Lnet/minecraft/network/chat/Component;"
        ),
        slice = @Slice(
            to = @At(
                value = "FIELD",
                target = "Lnet/minecraft/client/gui/Gui;titleTime:I")
        )
    )
    private void beforeTitle(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.TITLE_TEXT, guiGraphics, partialTick);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/SubtitleOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V")
    )
    private void beforeSubtitle(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.SUBTITLES, guiGraphics, partialTick);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getScoreboard()Lnet/minecraft/world/scores/Scoreboard;"
        )
    )
    private void beforeScoreboard(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.SCOREBOARD, guiGraphics, partialTick);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V",
            args = "ldc=chat"
        )
    )
    private void beforeChat(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.CHAT, guiGraphics, partialTick);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/KeyMapping;isDown()Z"
        )
    )
    private void beforePlayerList(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.PLAYER_LIST, guiGraphics, partialTick);
    }
}
