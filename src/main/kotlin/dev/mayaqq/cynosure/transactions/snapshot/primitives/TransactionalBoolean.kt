package dev.mayaqq.cynosure.transactions.snapshot.primitives

import dev.mayaqq.cynosure.transactions.TransactionContext
import dev.mayaqq.cynosure.transactions.snapshot.SnapshotHandler

public class TransactionalBoolean(initial: Boolean) : SnapshotHandler<Boolean>() {

    public var value: Boolean = initial
        private set

    context(TransactionContext)
    public fun set(newValue: Boolean): Boolean {
        val v = value
        updateSnapshots()
        value = newValue
        return v
    }

    context(TransactionContext)
    public fun negate(): Boolean {
        val v = value
        updateSnapshots()
        value = !value
        return v
    }

    override fun createSnapshot(): Boolean = value

    override fun readSnapshot(snapshot: Boolean) {
        value = snapshot
    }
}