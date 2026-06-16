package dev.mayaqq.cynosure.core.codecs.advancements

import com.mojang.serialization.*
import dev.mayaqq.cynosure.core.bytecodecs.toByteCodec
import dev.mayaqq.cynosure.core.codecs.Codecs
import net.minecraft.advancements.critereon.*
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

/**
 * Codecs for advancement predicets, to get a byte codec just use [toByteCodec] as these are by default
 * hardcoded to JSON
 */

public expect object PredicateCodecs : PredicateCodecProvider

public interface PredicateCodecProvider {

    public val BLOCK_CODEC: Codec<BlockPredicate>
    public val FLUID_CODEC: Codec<FluidPredicate>
    public val ENTITY_CODEC: Codec<EntityPredicate>
    public val ENTITY_TYPE_CODEC: Codec<EntityTypePredicate>
    public val PLAYER_CODEC: Codec<PlayerPredicate>
    public val ITEM_CODEC: Codec<ItemPredicate>
    public val ENCHANTMENT_CODEC: Codec<EnchantmentPredicate>
    public val DAMAGE_CODEC: Codec<DamagePredicate>
    public val DAMAGE_SOURCE_CODEC: Codec<DamageSourcePredicate>
    public val NBT_CODEC: Codec<NbtPredicate>
    public val LOCATION_CODEC: Codec<LocationPredicate>
    public val MOB_EFFECT_CODEC: Codec<MobEffectsPredicate>
    public fun <T> tag(registry: ResourceKey<Registry<T>>): Codec<TagPredicate<T>>
}