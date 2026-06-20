package dev.mayaqq.cynosure.neoforge.v1211.mixin;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.DynamicOps;
import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.world.LoottableEvents;
import dev.mayaqq.cynosure.loot.LootilsKt;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(ReloadableServerRegistries.class)
public abstract class ReloadableServerRegistriesMixin {
    /**
     * Due to possible cross-thread handling, this uses WeakHashMap instead of ThreadLocal.
     */
    @Unique
    private static final WeakHashMap<RegistryOps<JsonElement>, HolderLookup.Provider> WRAPPERS = new WeakHashMap<>();

    @WrapOperation(
            method = "reload",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/ReloadableServerRegistries$EmptyTagLookupWrapper;createSerializationContext(Lcom/mojang/serialization/DynamicOps;)Lnet/minecraft/resources/RegistryOps;"
            )
    )
    private static RegistryOps<JsonElement> storeOps(
            ReloadableServerRegistries.EmptyTagLookupWrapper instance,
            DynamicOps<JsonElement> dynamicOps,
            Operation<RegistryOps<JsonElement>> original
    ) {
        RegistryOps<JsonElement> created = original.call(instance, dynamicOps);
        WRAPPERS.put(created, instance);
        return created;
    }

    @WrapOperation(
            method = "reload",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/concurrent/CompletableFuture;thenApplyAsync(Ljava/util/function/Function;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"
            )
    )
    private static CompletableFuture<LayeredRegistryAccess<RegistryLayer>> removeOps(
            CompletableFuture<List<WritableRegistry<?>>> future,
            Function<? super List<WritableRegistry<?>>, ? extends LayeredRegistryAccess<RegistryLayer>> fn,
            Executor executor,
            Operation<CompletableFuture<LayeredRegistryAccess<RegistryLayer>>> original,
            @Local RegistryOps<JsonElement> ops
    ) {
        return original.call(future.thenApply(v -> {
            WRAPPERS.remove(ops);
            return v;
        }), fn, executor);
    }

    @WrapOperation(method = "lambda$scheduleElementParse$3", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
    private static <T> void modifyLootTable(Optional<T> optionalTable, Consumer<? super T> action, Operation<Void> original, @Local(argsOnly = true) ResourceLocation id, @Local(argsOnly = true) RegistryOps<JsonElement> ops) {
        original.call(optionalTable.map(table -> cynosure$modifyLootTable(table, id, ops)), action);
    }

    @Unique
    private static <T> T cynosure$modifyLootTable(T value, ResourceLocation id, RegistryOps<JsonElement> ops) {
        if (!(value instanceof LootTable table)) return value;

        if (table == LootTable.EMPTY) {
            // This is a special table and cannot be modified.
            return value;
        }

        // Invoke the REPLACE event for the current loot table.
        var replace = new LoottableEvents.Replace(id, table);
        MainBus.INSTANCE.post(replace);
        LootTable replacement = replace.getResult();

        if (replacement != null) {
            // Set the loot table to MODIFY to be the replacement loot table.
            // The MODIFY event will also see it as a replaced loot table via the source.
            table = replacement;
        }

        // Turn the current table into a modifiable builder and invoke the MODIFY event.
        LootTable.Builder builder = LootilsKt.copy(table);
        var modify = new LoottableEvents.Modify(id, builder);
        MainBus.INSTANCE.post(modify);

        return (T) builder.build();
    }

    @Inject(method = "lambda$scheduleElementParse$4", at = @At("RETURN"))
    private static <T> void onLootTablesLoaded(LootDataType<T> lootDataType, ResourceManager resourceManager, RegistryOps<JsonElement> registryOps, CallbackInfoReturnable<WritableRegistry<?>> cir) {
        if (lootDataType != LootDataType.TABLE) return;
        var allloaded = new LoottableEvents.AllLoaded(resourceManager);
        MainBus.INSTANCE.post(allloaded);
    }
}
