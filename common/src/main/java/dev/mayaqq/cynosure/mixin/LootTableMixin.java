package dev.mayaqq.cynosure.mixin;

import dev.mayaqq.cynosure.injection.ILootTable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(LootTable.class)
public class LootTableMixin implements ILootTable {

    @Shadow
    @Final
    private LootPool[] pools;

    @Shadow
    @Final
    private LootItemFunction[] functions;

    @Shadow
    @Final
    @Nullable
    private ResourceLocation randomSequence;

    @Override
    public @NotNull List<@NotNull LootPool> cynosure_getPools() {
        return List.of(this.pools);
    }

    @Override
    public @NotNull List<@NotNull LootItemFunction> cynosure_getFunctions() {
        return List.of(this.functions);
    }

    @Override
    public @NotNull ResourceLocation cynosure_getRandomSequnence() {
        return this.randomSequence;
    }
}
