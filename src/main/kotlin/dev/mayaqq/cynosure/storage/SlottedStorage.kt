package dev.mayaqq.cynosure.storage

import dev.mayaqq.cynosure.storage.resource.Resource
import dev.mayaqq.cynosure.storage.resource.ResourceStack
import dev.mayaqq.cynosure.transactions.TransactionContext

public interface SlottedStorage<R : Resource> : Storage<R>, Iterable<StorageSlot<R>> {

    public val size: Int

    public val contents: List<ResourceStack<R>>
        get() = map(StorageSlot<R>::contents)

    public operator fun get(index: Int): StorageSlot<R>

    context(TransactionContext)
    public fun insert(slot: Int, resource: R, amount: Long): Long = get(slot).insert(resource, amount)

    context(TransactionContext)
    public fun extract(slot: Int, resource: R, amount: Long): Long = get(slot).extract(resource, amount)
}