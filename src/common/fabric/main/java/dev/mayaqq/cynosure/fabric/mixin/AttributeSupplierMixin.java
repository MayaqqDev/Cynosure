package dev.mayaqq.cynosure.fabric.mixin;

import dev.mayaqq.cynosure.injection.IAttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(AttributeSupplier.class)
public class AttributeSupplierMixin implements IAttributeSupplier {
    @Shadow @Final private Map<Attribute, AttributeInstance> instances;

    @Override
    public @NotNull AttributeSupplier.Builder cynosure_toBuilder() {
        AttributeSupplier.Builder builder = AttributeSupplier.builder();
        ((AttributeSupplierBuilderAccessor) builder).getBuilder().putAll(this.instances);
        return builder;
    }
}
