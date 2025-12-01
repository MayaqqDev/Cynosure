package dev.mayaqq.cynosure.storage

import dev.mayaqq.cynosure.storage.resource.Resource
import dev.mayaqq.cynosure.storage.resource.ResourceStack
import dev.mayaqq.cynosure.transactions.snapshot.SnapshotHandler

public abstract class StorageSlot<R : Resource> : SnapshotHandler<ResourceStack<R>>(), Storage<R> {

    public abstract var contents: ResourceStack<R>
        protected set

    public open val resource: R
        get() = contents.resource

    public open val amount: Long
        get() = contents.amount

    public abstract fun getMaxAmount(resource: R): Long

    override fun createSnapshot(): ResourceStack<R> = contents

    override fun readSnapshot(snapshot: ResourceStack<R>) {
        contents = snapshot
    }
}