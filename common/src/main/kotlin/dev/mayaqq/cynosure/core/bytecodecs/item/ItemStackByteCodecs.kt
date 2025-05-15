package dev.mayaqq.cynosure.core.bytecodecs.item

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import dev.mayaqq.cynosure.core.bytecodecs.ByteCodecs
import dev.mayaqq.cynosure.core.codecs.fieldOf
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

public object ItemStackByteCodec : ByteCodec<ItemStack> by ByteCodec.BYTE.dispatch(ItemStackByteCodec::pickByteCodec, ItemStackByteCodec::getType) {

    @JvmField
    public val EMPTY_ITEM: ByteCodec<ItemStack> = ByteCodec.unit(ItemStack.EMPTY)

    @JvmField
    public val SINGLE_ITEM: ByteCodec<ItemStack> = ByteCodecs.ITEM.map(Item::getDefaultInstance, ItemStack::getItem)

    @JvmField
    public val ITEM_STACK_WITH_COUNT: ByteCodec<ItemStack> = ObjectByteCodec.create(
        ByteCodecs.ITEM fieldOf ItemStack::getItem,
        ByteCodec.INT fieldOf ItemStack::getCount,
        ::ItemStack
    )

    @JvmField
    public val ITEM_WITH_COUNT_AND_TAG: ByteCodec<ItemStack> = ObjectByteCodec.create(
        ByteCodecs.ITEM fieldOf ItemStack::getItem,
        ByteCodec.INT fieldOf ItemStack::getCount,
        ByteCodecs.NULLABLE_COMPOUND_TAG fieldOf ItemStack::getTag,
        fun(item, count, tag) = ItemStack(item, count).apply { tag?.let(::setTag) }
    )

    private fun pickByteCodec(id: Byte): ByteCodec<ItemStack> = when(id.toInt()) {
        1 -> SINGLE_ITEM
        2 -> ITEM_STACK_WITH_COUNT
        3 -> ITEM_WITH_COUNT_AND_TAG
        else -> EMPTY_ITEM
    }

    private fun getType(stack: ItemStack): Byte = when {
        stack.hasTag() -> 3
        stack.count > 1 -> 2
        !stack.isEmpty -> 1
        else -> 0
    }.toByte()
}


