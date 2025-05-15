package dev.mayaqq.cynosure.transactions

public interface TransactionListener

@TransactionsDsl
public fun interface OuterCloseListener : TransactionListener {
    public fun onOuterClose(result: TransactionResult)
}

@TransactionsDsl
public fun interface InnerCloseListener : TransactionListener {
    public fun TransactionContext.onClose(result: TransactionResult)
}

