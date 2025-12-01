package dev.mayaqq.cynosure.transactions.snapshot.primitives

import dev.mayaqq.cynosure.transactions.TransactionContext
import dev.mayaqq.cynosure.transactions.snapshot.SnapshotHandler

public class TransactionalFloat(
    initial: Float
) : SnapshotHandler<Float>() {

    public var value: Float = initial
        private set

    context(TransactionContext)
    public fun increment(by: Float = 1f): Float {
        updateSnapshots()
        val v = value
        value += by
        return v
    }

    context(TransactionContext)
    public fun decrement(by: Float = 1f): Float {
        updateSnapshots()
        val v = value
        value -= by
        return v
    }

    context(TransactionContext)
    public fun set(value: Float): Float {
        updateSnapshots()
        val v = this.value
        this.value = value
        return v
    }

    override fun createSnapshot(): Float = value

    override fun readSnapshot(snapshot: Float) {
        value = snapshot
    }
}