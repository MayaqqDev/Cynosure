package dev.mayaqq.cynosure.transactions

public enum class TransactionResult {
    COMMITED,
    ABORTED;

    public val isAborted: Boolean
        get() = this == ABORTED

    public val isCommited: Boolean
        get() = this == COMMITED
}