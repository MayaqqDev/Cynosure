package dev.mayaqq.cynosure.utils.result

public fun <A> A.success(): Result<A> = Result.success(this)

public fun Throwable.failure(): Result<Nothing> = Result.failure(this)

public fun String.failure(): Result<Nothing> = RuntimeException(this).failure()

public fun Result.Companion.failure(errorMessage: String): Result<Nothing> = RuntimeException(errorMessage).failure()

public inline fun Result.Companion.require(condition: Boolean, error: () -> Throwable): Result<Unit> =
    if(condition) Unit.success() else error().failure()

public fun Result.Companion.require(condition: Boolean, message: String): Result<Unit> = require(condition) { RuntimeException(message) }

/**
 * Wraps this value in a failed [Result] if the value is null,
 * otherwise a successful Result.
 */
public fun <A> A?.failureIfNull(): Result<A> =
    this?.success() ?: Result.failure(NoSuchElementException())

/**
 * Wraps this value in a failed [Result] using the given error [message] if the value is null,
 * otherwise a successful Result.
 */
public fun <A> A?.failureIfNull(message: String): Result<A> =
    this?.success() ?: message.failure()

public inline fun <A> A?.failureIfNull(f: () -> Exception): Result<A> =
    this?.success() ?: f().failure()

public inline fun <R, reified X : Throwable> runCatchingSpecific(func: () -> R): Result<R> {
    return try {
        func().success()
    } catch(t : Throwable) {
        // rethrow t if it isn't the correct type cs we can't use reified types in catch for some reason
        if(t is X) t.failure() else throw t
    }
}

public inline fun <T, R, reified X : Throwable> T.runCatchingSpecific(func: T.() -> R): Result<R> {
    return try {
        func().success()
    } catch(t : Throwable) {
        if(t is X) t.failure() else throw t
    }
}

public inline fun <T : AutoCloseable?, R> T.useCatching(block: T.() -> R): Result<R> {
    var exception: Throwable? = null
    try {
        return block(this).success()
    } catch (e: Throwable) {
        exception = e
        return e.failure()
    } finally {
        this.closeFinally(exception)
    }
}

public inline fun <T : AutoCloseable?, R, reified X : Throwable> T.useCatchingSpecific(block: T.() -> R): Result<R> {
    var exception: Throwable? = null
    try {
        return block(this).success()
    } catch (e: Throwable) {
        exception = e
        return if(e is X) e.failure() else throw e
    } finally {
        this.closeFinally(exception)
    }
}

// Copy of kotlin internal function
@PublishedApi
internal fun AutoCloseable?.closeFinally(cause: Throwable?): Unit = when {
    this == null -> {}
    cause == null -> close()
    else ->
        try {
            close()
        } catch (closeException: Throwable) {
            cause.addSuppressed(closeException)
        }
}