package dev.mayaqq.cynosure.transactions

public sealed interface TransactionContext {

    public val depth: Int

    public fun addCloseListener(listener: InnerCloseListener)

    public fun addOuterCloseListener(listener: OuterCloseListener)

    public operator fun get(depth: Int): Transaction?

}