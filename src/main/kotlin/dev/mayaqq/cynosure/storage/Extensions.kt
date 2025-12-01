package dev.mayaqq.cynosure.storage

import dev.mayaqq.cynosure.storage.resource.Resource
import dev.mayaqq.cynosure.transactions.TransactionContext

public fun <R : Resource> Storage<R>.insert(context: TransactionContext, resource: R, amount: Long) {
    return context.run { insert(resource, amount) }
}

public fun <R : Resource> Storage<R>.extract(context: TransactionContext, resource: R, amount: Long) {
    return context.run { extract(resource, amount) }
}