package dev.mayaqq.cynosure.forge.mixin;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.world.LoottableEvents;
import dev.mayaqq.cynosure.loot.LootilsKt;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mixin(LootDataManager.class)
public class LootDataManagerMixin {
    @Shadow private Map<LootDataId<?>, ?> elements;

    @ModifyReturnValue(
            method = "reload",
            at = @At("RETURN")
    )
    private CompletableFuture<Void> addCynosureLootTables(CompletableFuture<Void> original, @Local(argsOnly = true) ResourceManager manager) {
        return original.thenRun(() -> cynosure$applyCynosureLootTables(manager, (LootDataManager) (Object) this));
    }


    @Unique
    private void cynosure$applyCynosureLootTables(ResourceManager resourceManager, LootDataManager lootManager) {
        // The builder for the new LootManager.tables map with modified loot tables.
        // We're using an immutable map to match vanilla.
        ImmutableMap.Builder<LootDataId<?>, Object> newTables = ImmutableMap.builder();

        this.elements.forEach((dataKey, entry) -> {
            if (dataKey == LootDataManager.EMPTY_LOOT_TABLE_KEY) {
                // This is a special table and cannot be modified.
                // Vanilla also warns about that.
                newTables.put(dataKey, entry);
                return;
            }

            if (!(entry instanceof LootTable table)) {
                // We only want to modify loot tables
                newTables.put(dataKey, entry);
                return;
            }

            // Invoke the Replace event for the current loot table.
            var replace = new LoottableEvents.Replace(resourceManager, lootManager, dataKey.location(), table);
            MainBus.INSTANCE.post(replace);
            var replacement = replace.getResult();

            if (replacement != null) {
                table = replacement;
            }

            // Turn the current table into a modifiable builder and invoke the MODIFY event.
            LootTable.Builder builder = LootilsKt.copy(table);
            MainBus.INSTANCE.post(new LoottableEvents.Modify(resourceManager, lootManager, dataKey.location(), builder));

            // Turn the builder back into a loot table and store it in the new table.
            newTables.put(dataKey, builder.build());
        });

        this.elements = newTables.build();

        MainBus.INSTANCE.post(new LoottableEvents.AllLoaded(resourceManager, lootManager));
    }
}
