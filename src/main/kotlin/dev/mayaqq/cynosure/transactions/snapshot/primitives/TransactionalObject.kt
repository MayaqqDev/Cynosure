package dev.mayaqq.cynosure.transactions.snapshot.primitives

import dev.mayaqq.cynosure.transactions.TransactionContext
import dev.mayaqq.cynosure.transactions.snapshot.SnapshotHandler

public class TransactionalObject<T : Any>(
    initial: T?
) : SnapshotHandler<Any>() {

    public var value: T? = initial
        private set

    context(TransactionContext)
    public fun set(newValue: T?): T? {
        updateSnapshots()
        val v = value
        value = newValue
        return v
    }

    override fun createSnapshot(): Any = value ?: Null

    override fun readSnapshot(snapshot: Any) {
        value = when (snapshot) {
            Null -> null
            else -> snapshot as T
        }
    }

    private object Null
}