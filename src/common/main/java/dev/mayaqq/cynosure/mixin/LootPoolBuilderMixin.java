package dev.mayaqq.cynosure.mixin;

import dev.mayaqq.cynosure.injection.ILootPoolBuilder;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LootPool.Builder.class)
public class LootPoolBuilderMixin implements ILootPoolBuilder {

    @Shadow
    @Final
    private List<LootItemCondition> conditions;

    @Shadow
    @Final
    private List<LootPoolEntryContainer> entries;

    @Override
    public @NotNull List<@NotNull LootItemCondition> cynosure_getConditions() {
        return this.conditions;
    }

    @Override
    public @NotNull List<@NotNull LootPoolEntryContainer> cynosure_getEntries() {
        return this.entries;
    }
}
