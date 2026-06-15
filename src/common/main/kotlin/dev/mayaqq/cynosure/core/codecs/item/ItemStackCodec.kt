package dev.mayaqq.cynosure.core.codecs.item

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.Decoder
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.core.bytecodecs.item.ItemStackByteCodec
import dev.mayaqq.cynosure.core.codecs.Codecs
import dev.mayaqq.cynosure.core.codecs.fieldOf
import dev.mayaqq.cynosure.core.codecs.forGetter
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.*

public object ItemStackCodec : Codec<ItemStack>, Decoder<ItemStack> by Codecs.lazy({
    Codecs.alternatives(ItemStackCodec.MULTIPLE_ITEM, ItemStackCodec.SINGLE_ITEM, ItemStackCodec.EMPTY_STACK)
}) {

    public val EMPTY_STACK: Codec<ItemStack> = Codec.unit(ItemStack.EMPTY)

    public val SINGLE_ITEM: Codec<ItemStack> = BuiltInRegistries.ITEM.byNameCodec()
        .xmap(Item::getDefaultInstance, ItemStack::getItem)

    public val MULTIPLE_ITEM: Codec<ItemStack> = RecordCodecBuilder.create { it.group(
        BuiltInRegistries.ITEM.byNameCodec() fieldOf "item" forGetter ItemStack::getItem,
        Codec.INT.optionalFieldOf("count", 1) forGetter ItemStack::getCount,
        CompoundTag.CODEC.optionalFieldOf("nbt") forGetter { Optional.ofNullable(it.tag) }
    ).apply(it, fun(item, count, tag) = ItemStack(item, count).apply { tag.ifPresent(::setTag) })}

    public val NETWORK: ByteCodec<ItemStack> = ItemStackByteCodec

    override fun <T : Any> encode(input: ItemStack, ops: DynamicOps<T>, prefix: T): DataResult<T> {
        val codec = when {
            input.isEmpty -> EMPTY_STACK
            !input.hasTag() && input.count == 1 -> SINGLE_ITEM
            else -> MULTIPLE_ITEM
        }

        return codec.encode(input, ops, prefix)
    }
}

