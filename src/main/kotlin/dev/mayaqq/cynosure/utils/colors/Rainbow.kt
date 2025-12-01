package dev.mayaqq.cynosure.utils.colors

import dev.mayaqq.cynosure.Cynosure
import kotlinx.coroutines.*
import java.lang.ref.Cleaner
import java.lang.ref.Cleaner.Cleanable
import kotlin.reflect.KProperty


@Suppress("FunctionName")
public fun Rainbow(updateDelay: Long = 40L): ChangingColor = ChangingColor(Green, updateDelay) {
    var (r, g, b) = it
    if (r > 0 && b == 0) {
        r -= 1
        g += 1
    }
    if (g > 0 && r == 0) {
        g -= 1
        b += 1
    }
    if (b > 0 && g == 0) {
        b -= 1
        r += 1
    }
    return@ChangingColor Color(r, g, b, it.alpha)
}

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
private val ColorCoroutineScope: CoroutineScope = CoroutineScope(
    newSingleThreadContext("Color")
            + SupervisorJob()
            + CoroutineExceptionHandler { ctx, err ->
                Cynosure.error("Color update job {} failed", ctx.job, err)
            }
)

public class ChangingColor(
    initial: Color,
    private val updateDelay: Long,
    private val updater: Updater
) : AutoCloseable {

    public var color: Color = initial
        private set

    private val job: Job
    private val cleanable: Cleanable

    init {
        require(updateDelay >= 1L)
        job = ColorCoroutineScope.launch {
            while (true) {
                delay(updateDelay)
                color = updater.update(color)
            }
        }
        cleanable = CLEANER.register(this, JobCanceller(job))
    }


    public operator fun getValue(thisRef: Any?, property: KProperty<*>): Color = color

    override fun close() {
        cleanable.clean()
    }

    private companion object {
        private val CLEANER: Cleaner = Cleaner.create()
    }

    private class JobCanceller(val job: Job) : Runnable {
        override fun run() {
            job.cancel()
        }
    }

    public fun interface Updater {
        public fun update(color: Color): Color
    }
}