package dev.mayaqq.cynosure.internal

import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.injection.IAttributeSupplier
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.tags.TagKey
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

@CynosureInternal
internal object CynosureHooksImpl : CynosureHooks {

    override fun attributeSupplierToBuilder(supplier: AttributeSupplier): AttributeSupplier.Builder {
        return (supplier as IAttributeSupplier).cynosure_toBuilder()
    }

    override fun registerReloadListener(id: ResourceLocation, listener: PreparableReloadListener) {
        registerReloadListener(id, PackType.SERVER_DATA, listener)
    }


    internal fun registerReloadListener(id: ResourceLocation, type: PackType, listener: PreparableReloadListener) {
        val identifiable = object : IdentifiableResourceReloadListener {
            override fun reload(
                preparationBarrier: PreparableReloadListener.PreparationBarrier,
                resourceManager: ResourceManager,
                profilerFiller: ProfilerFiller,
                profilerFiller2: ProfilerFiller,
                executor: Executor,
                executor2: Executor
            ): CompletableFuture<Void> {
                return listener.reload(preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2)
            }

            override fun getFabricId(): ResourceLocation = id
        }

        ResourceManagerHelper.get(type).registerReloadListener(identifiable)
    }

    override fun getItemBurnTime(stack: ItemStack): Int {
        val mappings = FabricLoader.getInstance().mappingResolver
        val lookup = MethodHandles.privateLookupIn(AbstractFurnaceBlockEntity::class.java, MethodHandles.lookup())
        val klass = AbstractFurnaceBlockEntity::class.java

        val handle = lookup.findSpecial(
            klass,
            mappings.mapMethodName(
                "intermediary",
                klass.name,
                "method_11200",
                "(Lnet/minecraft/class_1799;)I"
            ),
            MethodType.methodType(Int::class.javaPrimitiveType, ItemStack::class.java),
            AbstractFurnaceBlockEntity::class.java
        )

        return handle.invokeExact(null, stack) as Int
    }

    override fun registerBurnTime(tag: TagKey<Item>, time: Int) {
        FuelRegistry.INSTANCE.add(tag, time)
    }

    override fun registerBurnTime(item: ItemLike, time: Int) {
        FuelRegistry.INSTANCE.add(item, time)
    }


}