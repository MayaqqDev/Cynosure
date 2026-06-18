package dev.mayaqq.cynosure.internal

import dev.mayaqq.cynosure.CynosureInternal
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraftforge.common.ForgeHooks

@CynosureInternal
internal object CynosureHooksImpl : CynosureHooks {

    internal val SERVER_RELOAD_LISTENERS: MutableMap<ResourceLocation, PreparableReloadListener> = mutableMapOf()

    internal val BURN_TIME_BY_ITEM: Object2IntMap<ItemLike> = Object2IntOpenHashMap()
    internal val BURN_TIME_BY_TAG: Object2IntMap<TagKey<Item>> = Object2IntOpenHashMap()

    override fun attributeSupplierToBuilder(supplier: AttributeSupplier): AttributeSupplier.Builder {
        return AttributeSupplier.Builder(supplier)
    }

    override fun registerReloadListener(id: ResourceLocation, listener: PreparableReloadListener) {
        require(!SERVER_RELOAD_LISTENERS.containsKey(id)) { "Listener with id $id already exits" }
        require(SERVER_RELOAD_LISTENERS[id] != listener) { "Attempting to register dupliate listener" }
        SERVER_RELOAD_LISTENERS[id] = listener
    }

    override fun registerBurnTime(item: ItemLike, time: Int) {
        BURN_TIME_BY_ITEM[item] = time
    }

    override fun registerBurnTime(tag: TagKey<Item>, time: Int) {
        BURN_TIME_BY_TAG[tag] = time
    }

    override fun getItemBurnTime(stack: ItemStack): Int = ForgeHooks.getBurnTime(stack, null)

}