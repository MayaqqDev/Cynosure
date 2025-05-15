@file:JvmName("DSL")
package dev.mayaqq.cynosure.transactions

import dev.mayaqq.cynosure.transactions.internal.LocalManager
import dev.mayaqq.cynosure.transactions.internal.TransactionManagerAccess
import dev.mayaqq.cynosure.transactions.internal.TransactionManagerAccess.result
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// TODO: Document
@TransactionsDsl
public inline fun transaction(action: Transaction.() -> Unit): TransactionResult {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    val transaction = TransactionManagerAccess.openOuter()
    transaction.use(action)
    return transaction.result
}

@TransactionsDsl
public inline fun TransactionContext.transaction(action: Transaction.() -> Unit): TransactionResult {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    val transaction =  TransactionManagerAccess.openInner(this)
    transaction.use(action)
    return transaction.result
}

public val transactionDepth: Int
    get() = LocalManager.depth