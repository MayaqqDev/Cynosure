package dev.mayaqq.cynosure.transactions.internal

import dev.mayaqq.cynosure.transactions.Transaction
import dev.mayaqq.cynosure.transactions.TransactionContext

/**
 * Published api access to internal [TransactionManager] for inline functions
 */
@PublishedApi
internal object TransactionManagerAccess {

    fun openOuter() = LocalManager.openOuter()

    fun openInner(current: TransactionContext): Transaction = (current as TransactionManager.LinkedTransaction).openNested()

    val Transaction.result
        get() = (this as TransactionManager.LinkedTransaction).result
}