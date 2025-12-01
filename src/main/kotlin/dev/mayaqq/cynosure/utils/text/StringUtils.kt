package dev.mayaqq.cynosure.utils.text

import dev.mayaqq.cynosure.utils.function.ToFloatFunction
import java.text.BreakIterator
import java.util.*

// Using ToFloatFunction to avoid wrapping
public fun String.wrap(
    locale: Locale,
    maxLength: Float = 200.0f,
    lengthGetter: ToFloatFunction<String> = ToFloatFunction { it.length.toFloat() }
) {

    val breaks = BreakIterator.getWordInstance()
}