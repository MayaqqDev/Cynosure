package dev.mayaqq.cynosure.forge.v1211.mixin;

import dev.mayaqq.cynosure.injection.ILootTable;
import kotlin.OptionalExpectation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;

@Mixin(LootTable.class)
public class LootTableMixin implements ILootTable {

    @Shadow
    @Final
    private List<LootPool> pools;

    @Shadow
    @Final
    private List<LootItemFunction> functions;

    @Shadow
    @Final
    @Nullable
    private Optional<ResourceLocation> randomSequence;

    @Override
    public @NotNull List<@NotNull LootPool> cynosure_getPools() {
        return this.pools;
    }

    @Override
    public @NotNull List<@NotNull LootItemFunction> cynosure_getFunctions() {
        return this.functions;
    }

    @Override
    public @NotNull ResourceLocation cynosure_getRandomSequnence() {
        //TODO: its probably always not null... right!
        return this.randomSequence.get();
    }
}
