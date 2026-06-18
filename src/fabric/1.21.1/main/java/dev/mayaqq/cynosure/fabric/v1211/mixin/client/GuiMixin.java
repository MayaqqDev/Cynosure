package dev.mayaqq.cynosure.fabric.v1211.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.mayaqq.cynosure.client.events.render.BeginHudRenderEvent;
import dev.mayaqq.cynosure.client.render.gui.HudOverlay;
import dev.mayaqq.cynosure.client.render.gui.HudOverlayRegistry;
import dev.mayaqq.cynosure.client.render.gui.VanillaHud;
import dev.mayaqq.cynosure.events.api.MainBus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow protected abstract Player getCameraPlayer();

    @Unique
    private Map<ResourceLocation, List<HudOverlay>> hudOverlays = null;

    @Unique
    private void renderPhaseOverlays(ResourceLocation phase, GuiGraphics guiGraphics, float partialTick) {
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
    private void onBeginRenderHud(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        var event = new BeginHudRenderEvent((Gui) (Object) this, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
        if(MainBus.INSTANCE.post(event)) ci.cancel();
    }

    @Inject(
        method = "renderCameraOverlays",
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
    private void beforeRenderVignette(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.VIGNETTE, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @ModifyExpressionValue(
        method = "renderCameraOverlays",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"
        )
    )
    private boolean renderLayersIfNotFirstPerson(boolean original, @Local(argsOnly = true) GuiGraphics graphics, @Local(argsOnly = true) DeltaTracker delta) {
        if(!original) {
            renderPhaseOverlays(VanillaHud.SPYGLASS, graphics, delta.getGameTimeDeltaPartialTick(false));
            renderPhaseOverlays(VanillaHud.HELMET, graphics, delta.getGameTimeDeltaPartialTick(false));
        }
        return original;
    }

    @Inject(
        method = "renderCameraOverlays",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isScoping()Z"
        )
    )
    private void beforeRenderSpyGlass(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.SPYGLASS, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Definition(id = "CARVED_PUMPKIN", field = "Lnet/minecraft/world/level/block/Blocks;CARVED_PUMPKIN:Lnet/minecraft/world/level/block/Block;")
    @Definition(id = "asItem", method = "Lnet/minecraft/world/level/block/Block;asItem()Lnet/minecraft/world/item/Item;")
    @Definition(id = "is", method = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
    @Expression("?.is(CARVED_PUMPKIN.asItem())")
    @Inject(
        method = "renderCameraOverlays",
        at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private void beforePumpkinOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.HELMET, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Inject(
        method = "renderCameraOverlays",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getTicksFrozen()I"
        )
    )
    private void beforeFreezingOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.FROSTBITE, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Definition(id = "lerp", method = "Lnet/minecraft/util/Mth;lerp(FFF)F")
    @Definition(id = "oSpinningEffectIntensity", field = "Lnet/minecraft/client/player/LocalPlayer;oSpinningEffectIntensity:F")
    @Definition(id = "spinningEffectIntensity", field = "Lnet/minecraft/client/player/LocalPlayer;spinningEffectIntensity:F")
    @Expression("lerp(?, ?.oSpinningEffectIntensity, ?.spinningEffectIntensity)")
    @Inject(
        method = "renderCameraOverlays",
        at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER)
    )
    private void beforePortalOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.PORTAL, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Inject(
        method = "renderHotbarAndDecorations",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;getPlayerMode()Lnet/minecraft/world/level/GameType;"
        )
    )
    private void beforeHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.HOTBAR, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    /* TODO: This jsut does not work with the current system
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
     */

    @Inject(
        method = "renderCrosshair",
        at = @At("HEAD")
    )
    private void beforeRenderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.CROSSHAIR, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Inject(
        method = "lambda$new$0",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"
        )
    )
    private void beforeBossOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.BOSS_BAR, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @ModifyExpressionValue(
        method = "renderHotbarAndDecorations",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;canHurtPlayer()Z"
        )
    )
    private boolean ifHealthAndFoodHidden(boolean original, @Local(argsOnly = true) GuiGraphics graphics, @Local(argsOnly = true) DeltaTracker delta) {
        if (!original) {
            renderPhaseOverlays(VanillaHud.PLAYER_HEALTH, graphics, delta.getGameTimeDeltaPartialTick(false));
            renderPhaseOverlays(VanillaHud.FOOD_LEVEL, graphics, delta.getGameTimeDeltaPartialTick(false));
        }
        capturedPartialTick = delta;
        return original;
    }

    @Unique
    DeltaTracker capturedPartialTick;

    @Inject(
        method = "renderHotbarAndDecorations",
        at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/Gui;renderPlayerHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"
        )
    )
    private void beforePlayerHealth(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.PLAYER_HEALTH, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
        if(getCameraPlayer() == null) renderPhaseOverlays(VanillaHud.FOOD_LEVEL, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
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
        renderPhaseOverlays(VanillaHud.FOOD_LEVEL, guiGraphics, capturedPartialTick.getGameTimeDeltaPartialTick(false));
    }

    @Inject(
        method = "renderHotbarAndDecorations",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphics;)V")
    )
    private void beforeVehicleHealth(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.MOUNT_HEALTH, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Inject(
        method = "renderHotbarAndDecorations",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;jumpableVehicle()Lnet/minecraft/world/entity/PlayerRideableJumping;"
        )
    )
    private void beforeJumpBar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.JUMP_BAR, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Inject(
        method = "renderHotbarAndDecorations",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;isExperienceBarVisible()Z"
        )
    )
    private void beforeXpBar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.XP_BAR, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Inject(
        method = "renderHotbarAndDecorations",
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
    private void beforeSpectatorTooltip(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.ITEM_NAME, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Inject(
        method = "renderSleepOverlay",
        at = @At("HEAD")
    )
    private void beforeSleep(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.SLEEP_FADE, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Inject(
        method = "renderEffects",
        at = @At("HEAD")
    )
    private void beforeEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.EFFECTS, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Inject(
        method = "lambda$new$1",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;showDebugScreen()Z"
        )
    )
    private void beforeDebug(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.DEBUG, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    /* TODO: Again, it doesnt wokr this way anymore :(
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
     */

    @Inject(
        method = "renderOverlayMessage",
        at = @At("HEAD")
    )
    private void beforeOverlayMessageString(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.OVERLAY_MESSAGE, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Inject(
        method = "renderTitle",
        at = @At("HEAD")
    )
    private void beforeTitle(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.TITLE_TEXT, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Inject(
        method = "lambda$new$2",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/SubtitleOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V")
    )
    private void beforeSubtitle(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.SUBTITLES, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Inject(
        method = "renderScoreboardSidebar",
        at = @At("HEAD")
    )
    private void beforeScoreboard(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.SCOREBOARD, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Inject(
        method = "renderChat",
        at = @At("HEAD")
    )
    private void beforeChat(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.CHAT, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Inject(
        method = "renderTabList",
        at = @At("HEAD")
    )
    private void beforePlayerList(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        renderPhaseOverlays(VanillaHud.PLAYER_LIST, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }
}
