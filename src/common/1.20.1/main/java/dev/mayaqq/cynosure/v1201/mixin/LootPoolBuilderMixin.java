package dev.mayaqq.cynosure.v1201.mixin;

import dev.mayaqq.cynosure.injection.ILootPoolBuilder;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

// Mixin check: doesn't work

@Mixin(LootPool.Builder.class)
public class LootPoolBuilderMixin implements ILootPoolBuilder {

    @Shadow
    @Final
    private List<LootItemCondition> conditions;

    @Shadow
    @Final
    private List<LootPoolEntryContainer> entries;

    @Override
    public void cynosure_addConditions(@NotNull List<? extends @NotNull LootItemCondition> conditionList) {
        conditions.addAll(conditionList);
    }

    @Override
    public void cynosure_addEntries(@NotNull List<? extends @NotNull LootPoolEntryContainer> entryList) {
        entries.addAll(entryList);
    }
}
