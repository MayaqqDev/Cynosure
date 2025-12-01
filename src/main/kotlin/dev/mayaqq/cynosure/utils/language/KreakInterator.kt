package dev.mayaqq.cynosure.utils.language

import java.text.BreakIterator
import java.util.Locale

public fun String.words(locale: Locale = Locale.ROOT): List<String> =
    buildList {
        val iterator = BreakIterator.getLineInstance(locale)
        iterator.setText(this@words)
        var start = iterator.first()
        var end = iterator.next()
        while (end != BreakIterator.DONE) {
            add(this@words.substring(start, end))
            start = end
            end = iterator.next()
        }
    }