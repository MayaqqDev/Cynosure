package dev.mayaqq.cynosure.v1211.mixin;

import dev.mayaqq.cynosure.injection.ILootTableBuilder;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

// Mixin check: should work

@Mixin(LootTable.Builder.class)
public class LootTableBuilderMixin implements ILootTableBuilder {
    @Shadow
    @Final
    private List<LootPool> pools;

    @Shadow
    @Final
    private List<LootItemFunction> functions;

    @Override
    public void cynosure_apply(@NotNull List<@NotNull LootItemFunction> functions) {
        this.functions.addAll(functions);
    }

    @Override
    public void cynosure_addPools(@NotNull List<? extends @NotNull LootPool> poolList) {
        pools.addAll(poolList);
    }

    @Override
    public void cynosure_addFunctions(@NotNull List<? extends @NotNull LootItemFunction> functionList) {
        functions.addAll(functionList);
    }
}
