package dev.mayaqq.cynosure.core.codecs.item

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
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
import java.util.function.Function

public object ItemStackCodec : Codec<ItemStack> by Codecs.lazy({ Codecs.alternatives(
    ItemStackCodec.EMPTY_STACK,
    ItemStackCodec.SINGLE_ITEM,
    ItemStackCodec.MULTIPLE_ITEM
) }) {

    public val EMPTY_STACK: Codec<ItemStack> = Codec.unit(ItemStack.EMPTY)
        .flatComapMap(
            Function.identity(),
            fun(stack) = if (stack != ItemStack.EMPTY) DataResult.error { "Stack is not empty" } else DataResult.success(stack)
        )

    public val SINGLE_ITEM: Codec<ItemStack> = BuiltInRegistries.ITEM.byNameCodec()
        .flatComapMap(
            Item::getDefaultInstance,
            fun(stack) = if (stack.count > 1 || stack.hasTag()) DataResult.error { "Stack contains more than one item or has a tag" }
                else DataResult.success(stack.item)
        )

    public val MULTIPLE_ITEM: Codec<ItemStack> = RecordCodecBuilder.create { it.group(
        BuiltInRegistries.ITEM.byNameCodec() fieldOf "item" forGetter ItemStack::getItem,
        Codec.INT.optionalFieldOf("count", 1) forGetter ItemStack::getCount,
        CompoundTag.CODEC.optionalFieldOf("nbt") forGetter { Optional.ofNullable(it.tag) }
    ).apply(it, fun(item, count, tag) = ItemStack(item, count).apply { tag.ifPresent(::setTag) })}

    public val NETWORK: ByteCodec<ItemStack> = ItemStackByteCodec

}

