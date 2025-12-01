package dev.mayaqq.cynosure.transactions.internal

import dev.mayaqq.cynosure.transactions.*
import dev.mayaqq.cynosure.utils.getValue
import dev.mayaqq.cynosure.utils.result.UNIT
import dev.mayaqq.cynosure.utils.result.failure

internal val LocalManager: TransactionManager by ThreadLocal.withInitial(::TransactionManager)

internal class TransactionManager {

    private val thread: Thread = Thread.currentThread()
    private var outerTransaction: LinkedTransaction? = null
    private var currentTransaction: LinkedTransaction? = null

    private val outerListeners: MutableList<OuterCloseListener> = mutableListOf()

    val lifecycle: Transaction.Lifecycle
        get() = currentTransaction?.lifecycle ?: Transaction.Lifecycle.CLOSED

    val depth: Int
        get() = if (currentTransaction == null) -1 else currentTransaction!!.depth

    fun openOuter(): Transaction {
        check(outerTransaction == null) { "Attempted to open outer transaction inside another transaction" }
        val transaction = LinkedTransaction(0, null)
        outerTransaction = transaction
        currentTransaction = transaction
        transaction.lifecycle = Transaction.Lifecycle.OPEN
        return transaction
    }

    fun validateThread() {
        val current = Thread.currentThread()
        check(current == thread) { "Attempted to access transation from thread ${thread.name} on ${current.name}" }
    }

    @DelicateTransactionApi
    fun getCurrent(): Transaction? {
        validateThread()
        return currentTransaction?.takeIf { it.lifecycle == Transaction.Lifecycle.OPEN }
    }

    internal inner class LinkedTransaction(
        nestingDepth: Int,
        private val parentTransaction: LinkedTransaction?
    ) : Transaction(nestingDepth) {

        private lateinit var listeners: MutableList<InnerCloseListener>

        override var lifecycle: Lifecycle = Lifecycle.CLOSED
        var result: TransactionResult = TransactionResult.ABORTED
            private set

        override fun close(result: TransactionResult) {
            validateCurrentTransaction()
            validateOpen()

            lifecycle = Lifecycle.CLOSING
            this.result = result

            var closeResult: Result<Unit> = Result.UNIT

            if (::listeners.isInitialized) {
                for (index in listeners.size downTo 0) {
                    listeners[index].runCatching { (this@LinkedTransaction as TransactionContext).onClose(result) }
                        .onFailure {
                            (closeResult.exceptionOrNull() ?: RuntimeException("Transaction error")
                                .also { closeResult = it.failure() })
                                .addSuppressed(it)
                        }
                }

                listeners.clear()
            }

            if (this === outerTransaction) {
                lifecycle = Lifecycle.OUTER_CLOSING
                for (index in outerListeners.size downTo 0) {
                    outerListeners[index].runCatching { this@runCatching.onOuterClose(result) }
                        .onFailure { (closeResult.exceptionOrNull() ?: RuntimeException("Transaction error").also { closeResult = it.failure() }).addSuppressed(it) }
                }
                outerTransaction = null
                outerListeners.clear()
            }

            currentTransaction = parentTransaction
            lifecycle = Lifecycle.CLOSED
            // Throw exception if any happened during closing once everything has been safely closed
            closeResult.getOrThrow()
        }

        override fun addCloseListener(listener: InnerCloseListener) {
            validateThread()
            validateOpen()
            if (!::listeners.isInitialized) listeners = mutableListOf()
            listeners.add(listener)
        }

        override fun addOuterCloseListener(listener: OuterCloseListener) {
            validateThread()
            outerListeners.add(listener)
        }

        override fun get(depth: Int): Transaction? {
            validateThread()
            return when (depth) {
                this.depth -> this
                this.depth - 1 -> parentTransaction
                else -> {
                    var transaction = parentTransaction
                    while (transaction != null) {
                        if (transaction.depth == depth) break
                        transaction = transaction.parentTransaction
                    }
                    transaction
                }
            }
        }

        fun openNested(): Transaction {
            validateThread()
            validateCurrentTransaction()
            val transaction = LinkedTransaction(depth + 1, this)
            currentTransaction = transaction
            return transaction
        }

        private fun validateCurrentTransaction() {
            validateThread()
            check(this === currentTransaction) { "Attempted to call transaction function on a transaction with depth $depth but current depth is ${currentTransaction?.depth}" }
        }

        private fun validateOpen() = check(lifecycle == Lifecycle.OPEN) { "Attempted to access transaction functions on a $lifecycle transaction" }

    }
}