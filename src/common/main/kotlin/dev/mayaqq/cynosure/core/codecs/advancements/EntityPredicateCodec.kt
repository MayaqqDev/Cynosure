package dev.mayaqq.cynosure.core.codecs.advancements

import com.mojang.serialization.*
import dev.mayaqq.cynosure.core.bytecodecs.toByteCodec
import dev.mayaqq.cynosure.core.codecs.Codecs
import net.minecraft.advancements.critereon.*
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

/**
 * Codecs for advancement predicets, to get a byte codec just use [toByteCodec] as these are by default
 * hardcoded to json
 */
// TODO: Maybe make actual codecs for some of these? Probably not as that could have compat issues
public object PredicateCodecs {

    @JvmField
    public val BLOCK: Codec<BlockPredicate> = Codecs.json(BlockPredicate::serializeToJson, BlockPredicate::fromJson)

    @JvmField
    public val FLUID: Codec<FluidPredicate> = Codecs.json(FluidPredicate::serializeToJson, FluidPredicate::fromJson)

    @JvmField
    public val ENTITY: Codec<EntityPredicate> = Codecs.json(EntityPredicate::serializeToJson, EntityPredicate::fromJson)

    @JvmField
    public val ENTITY_TYPE: Codec<EntityTypePredicate> = Codecs.json(EntityTypePredicate::serializeToJson, EntityTypePredicate::fromJson)

    @JvmField
    public val PLAYER: Codec<PlayerPredicate> = Codecs.json(PlayerPredicate::serialize, fun(json) = PlayerPredicate.fromJson(json.asJsonObject))

    @JvmField
    public val ITEM: Codec<ItemPredicate> = Codecs.json(ItemPredicate::serializeToJson, ItemPredicate::fromJson)

    @JvmField
    public val ENCHANTMENT: Codec<EnchantmentPredicate> = Codecs.json(EnchantmentPredicate::serializeToJson, EnchantmentPredicate::fromJson)

    @JvmField
    public val DAMAGE: Codec<DamagePredicate> = Codecs.json(DamagePredicate::serializeToJson, DamagePredicate::fromJson)

    @JvmField
    public val DAMAGE_SOURCE: Codec<DamageSourcePredicate> = Codecs.json(DamageSourcePredicate::serializeToJson, DamageSourcePredicate::fromJson)

    @JvmField
    public val NBT: Codec<NbtPredicate> = Codecs.json(NbtPredicate::serializeToJson, NbtPredicate::fromJson)

    @JvmField
    public val LOCATION: Codec<LocationPredicate> = Codecs.json(LocationPredicate::serializeToJson, LocationPredicate::fromJson)

    @JvmField
    public val MOB_EFFECT: Codec<MobEffectsPredicate> = Codecs.json(MobEffectsPredicate::serializeToJson, MobEffectsPredicate::fromJson)

    @JvmStatic
    public fun <T> tag(registry: ResourceKey<Registry<T>>): Codec<TagPredicate<T>> = Codecs.json(
        TagPredicate<T>::serializeToJson,
        fun(json) = TagPredicate.fromJson(json, registry)
    )
}