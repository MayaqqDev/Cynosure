package dev.mayaqq.cynosure.transactions

import dev.mayaqq.cynosure.transactions.internal.LocalManager
import org.jetbrains.annotations.ApiStatus.NonExtendable

@NonExtendable
public abstract class Transaction protected constructor(
    override val depth: Int
) : TransactionContext, AutoCloseable {

    public abstract val lifecycle: Lifecycle

    // val variants so u can use commit and abort like keywords
    @TransactionsDsl
    public inline val commit: Unit
        @JvmName("doCommit") get() { commit() }

    @TransactionsDsl
    public inline val abort: Unit
        @JvmName("doAbort") get() { abort() }

    @TransactionsDsl
    public fun commit() {
        close(TransactionResult.COMMITED)
    }

    @TransactionsDsl
    public fun abort() {
        close(TransactionResult.ABORTED)
    }

    override fun close() {
        if (lifecycle == Lifecycle.OPEN) abort()
    }

    protected abstract fun close(result: TransactionResult)

    public enum class Lifecycle {
        CLOSED,
        OPEN,
        CLOSING,
        OUTER_CLOSING
    }

    public companion object {

        @DelicateTransactionApi
        public val current: Transaction?
            get() = LocalManager.getCurrent()

        public val isOpen: Boolean
            get() = LocalManager.lifecycle == Lifecycle.OPEN

        public val lifecycle: Lifecycle
            get() = LocalManager.lifecycle

    }
}