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

    public override val BLOCK_CODEC: Codec<BlockPredicate> = Codecs.json(BlockPredicate::serializeToJson, BlockPredicate::fromJson)

    public override val FLUID_CODEC: Codec<FluidPredicate> = Codecs.json(FluidPredicate::serializeToJson, FluidPredicate::fromJson)

    public override val ENTITY_CODEC: Codec<EntityPredicate> = Codecs.json(EntityPredicate::serializeToJson, EntityPredicate::fromJson)

    public override val ENTITY_TYPE_CODEC: Codec<EntityTypePredicate> = Codecs.json(EntityTypePredicate::serializeToJson, EntityTypePredicate::fromJson)

    public override val PLAYER_CODEC: Codec<PlayerPredicate> = Codecs.json(PlayerPredicate::serialize, fun(json) = PlayerPredicate.fromJson(json.asJsonObject))

    public override val ITEM_CODEC: Codec<ItemPredicate> = Codecs.json(ItemPredicate::serializeToJson, ItemPredicate::fromJson)

    public override val ENCHANTMENT_CODEC: Codec<EnchantmentPredicate> = Codecs.json(EnchantmentPredicate::serializeToJson, EnchantmentPredicate::fromJson)

    public override val DAMAGE_CODEC: Codec<DamagePredicate> = Codecs.json(DamagePredicate::serializeToJson, DamagePredicate::fromJson)

    public override val DAMAGE_SOURCE_CODEC: Codec<DamageSourcePredicate> = Codecs.json(DamageSourcePredicate::serializeToJson, DamageSourcePredicate::fromJson)

    public override val NBT_CODEC: Codec<NbtPredicate> = Codecs.json(NbtPredicate::serializeToJson, NbtPredicate::fromJson)

    public override val LOCATION_CODEC: Codec<LocationPredicate> = Codecs.json(LocationPredicate::serializeToJson, LocationPredicate::fromJson)

    public override val MOB_EFFECT_CODEC: Codec<MobEffectsPredicate> = Codecs.json(MobEffectsPredicate::serializeToJson, MobEffectsPredicate::fromJson)

    public override fun <T> tag(registry: ResourceKey<Registry<T>>): Codec<TagPredicate<T>> = Codecs.json(
        TagPredicate<T>::serializeToJson,
        fun(json) = TagPredicate.fromJson(json, registry)
    )
}