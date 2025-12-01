package dev.mayaqq.cynosure.transactions.snapshot

import dev.mayaqq.cynosure.transactions.InnerCloseListener
import dev.mayaqq.cynosure.transactions.OuterCloseListener
import dev.mayaqq.cynosure.transactions.TransactionContext
import dev.mayaqq.cynosure.transactions.TransactionResult

public abstract class SnapshotHandler<T : Any> : InnerCloseListener, OuterCloseListener {
    private val snapshots: MutableList<T?> = mutableListOf()

    protected abstract fun createSnapshot(): T

    protected abstract fun readSnapshot(snapshot: T)

    protected open fun discardSnapshot(snapshot: T) {}

    protected open fun onFinalCommit() {}

    protected fun TransactionContext.updateSnapshots() {
        if (snapshots.getOrNull(depth) == null) {
            if (snapshots.size < depth) {
                val nulls = arrayOfNulls<Any>(depth - snapshots.size)
                snapshots.addAll(nulls as Array<T?>)
            }
            snapshots[depth] = createSnapshot()
            addOuterCloseListener(this@SnapshotHandler)
        }
    }

    final override fun TransactionContext.onClose(result: TransactionResult) {
        val snapshot = snapshots.set(depth, null) ?: return
        when {
            result == TransactionResult.ABORTED -> {
                readSnapshot(snapshot)
                discardSnapshot(snapshot)
            }
            depth > 0 -> {
                if (snapshots.getOrNull(depth -1) == null) {
                    snapshots[depth - 1] = snapshot
                    this[depth - 1]?.addCloseListener(this@SnapshotHandler)
                } else {
                    discardSnapshot(snapshot)
                }
            }
            else -> {
                discardSnapshot(snapshot)
                addOuterCloseListener(this@SnapshotHandler)
            }
        }
    }

    final override fun onOuterClose(result: TransactionResult) {
        onFinalCommit()
    }
}