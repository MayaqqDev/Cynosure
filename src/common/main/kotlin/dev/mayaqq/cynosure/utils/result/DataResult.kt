package dev.mayaqq.cynosure.utils.result

import com.mojang.serialization.DataResult

public fun <T> Result<T>.toDataResult(): DataResult<T> =
    fold({ DataResult.success(it) }, { DataResult.error(it::message) })

public fun <T> DataResult<T>.toKtResult(allowPartial: Boolean = false): Result<T> =
    get().map(
        { it.success() },
        {
            if (allowPartial) {
                val opt = resultOrPartial {}
                if (opt.isPresent) opt.get().success() else RuntimeException(it.message()).failure()
            } else RuntimeException(it.message()).failure()
        }
    )
