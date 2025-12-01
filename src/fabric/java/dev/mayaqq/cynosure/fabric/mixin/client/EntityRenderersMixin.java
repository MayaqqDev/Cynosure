package dev.mayaqq.cynosure.fabric.mixin.client;

import dev.mayaqq.cynosure.Cynosure;
import dev.mayaqq.cynosure.client.events.entity.RenderLayerRegistrationEvent;
import dev.mayaqq.cynosure.events.api.MainBus;
import kotlin.Unit;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderersMixin {

    @Shadow private Map<String, EntityRenderer<? extends Player>> playerRenderers;

    @Shadow private Map<EntityType<?>, EntityRenderer<?>> renderers;

    @Shadow @Final private EntityModelSet entityModels;

    @Inject(
        method = "onResourceManagerReload",
        at = @At("RETURN")
    )
    private void addAdditionallayers(ResourceManager resourceManager, CallbackInfo ci) {
        RenderLayerRegistrationEvent event = new RenderLayerRegistrationEvent((EntityRenderDispatcher) (Object) this, entityModels, new RenderLayerRegistrationEvent.Context() {
            @Override
            public @Nullable EntityRenderer<? extends Player> getSkin(@NotNull String name) {
                return playerRenderers.get(name);
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T extends LivingEntity> LivingEntityRenderer<T, EntityModel<T>> getEntity(@NotNull EntityType<T> entity) {
                if (renderers.get(entity) instanceof LivingEntityRenderer<?, ?> renderer)
                    return (LivingEntityRenderer<T, EntityModel<T>>) renderer;
                return null;
            }
        });

        MainBus.INSTANCE.post(event, null, (errer) -> {
            Cynosure.INSTANCE.error("Error registering entity layers", errer);
            return Unit.INSTANCE;
        });
    }
}
