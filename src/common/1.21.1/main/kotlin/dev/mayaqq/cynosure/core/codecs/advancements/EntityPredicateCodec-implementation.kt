package dev.mayaqq.cynosure.core.codecs.advancements

import com.mojang.serialization.Codec
import dev.mayaqq.cynosure.core.codecs.Codecs
import net.minecraft.advancements.critereon.BlockPredicate
import net.minecraft.advancements.critereon.DamagePredicate
import net.minecraft.advancements.critereon.DamageSourcePredicate
import net.minecraft.advancements.critereon.EnchantmentPredicate
import net.minecraft.advancements.critereon.EntityPredicate
import net.minecraft.advancements.critereon.EntityTypePredicate
import net.minecraft.advancements.critereon.FluidPredicate
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.advancements.critereon.LocationPredicate
import net.minecraft.advancements.critereon.MobEffectsPredicate
import net.minecraft.advancements.critereon.NbtPredicate
import net.minecraft.advancements.critereon.PlayerPredicate
import net.minecraft.advancements.critereon.TagPredicate
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

public actual object PredicateCodecs : PredicateCodecProvider {

    public override val BLOCK_CODEC: Codec<BlockPredicate> = BlockPredicate.CODEC

    public override val FLUID_CODEC: Codec<FluidPredicate> = FluidPredicate.CODEC

    public override val ENTITY_CODEC: Codec<EntityPredicate> = EntityPredicate.CODEC

    public override val ENTITY_TYPE_CODEC: Codec<EntityTypePredicate> = EntityTypePredicate.CODEC

    public override val PLAYER_CODEC: Codec<PlayerPredicate> = PlayerPredicate.CODEC.codec()

    public override val ITEM_CODEC: Codec<ItemPredicate> = ItemPredicate.CODEC

    public override val ENCHANTMENT_CODEC: Codec<EnchantmentPredicate> = EnchantmentPredicate.CODEC

    public override val DAMAGE_CODEC: Codec<DamagePredicate> = DamagePredicate.CODEC

    public override val DAMAGE_SOURCE_CODEC: Codec<DamageSourcePredicate> = DamageSourcePredicate.CODEC

    public override val NBT_CODEC: Codec<NbtPredicate> = NbtPredicate.CODEC

    public override val LOCATION_CODEC: Codec<LocationPredicate> = LocationPredicate.CODEC

    public override val MOB_EFFECT_CODEC: Codec<MobEffectsPredicate> = MobEffectsPredicate.CODEC

    public override fun <T> tag(registry: ResourceKey<Registry<T>>): Codec<TagPredicate<T>> = TagPredicate.codec(registry)
}