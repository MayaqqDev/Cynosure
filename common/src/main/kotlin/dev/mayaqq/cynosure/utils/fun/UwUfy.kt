@file:JvmName("UwUfy")
package dev.mayaqq.cynosure.utils.`fun`


private val PHRASES = arrayOf(
    "UwU",
    "owo",
    "OwO",
    "uwu",
    ">w<",
    "^w^",
    ":3",
    "^-^",
    "^_^",
    "^w^",
    ":3"
)

@JvmOverloads
public fun String.uwufy(first: Boolean = true, last: Boolean = true, forceUppercase: Boolean = false): String {
    var input = this
    val stringLength = input.length
    // Replace 'r' and 'l' with 'w', and 'R' and 'L' with 'W'
    // Replace 'ove' with 'uv' and 'OVE' with 'UV'
    // Replace 'o' with 'owo' and 'O' with 'OwO'
    // Replace repeated exclamation marks and question marks
    input = input
        .replace("[rl]".toRegex(), "w").replace("[RL]".toRegex(), "W")
        .replace("ove".toRegex(), "uv").replace("OVE".toRegex(), "UV")
        .replace("o".toRegex(), "owo").replace("O".toRegex(), "OwO")
        .replace("!".toRegex(), "!!!").replace("\\?".toRegex(), "???")

    // Convert to uppercase
    if (stringLength % 3 == 0 || forceUppercase) {
        input = input.uppercase()
    }

    input = input.replace(Regex("%(\\p{L})")) { m -> "%" + m.groupValues[1].lowercase() }
    input = input.replace(Regex("\\$(\\p{L})")) { m -> "\\$" + m.groupValues[1].lowercase() }

    input = if (stringLength % 2 == 0) {
        // Add more letters to the end of words (Not numbers!)
        input.replace("(\\p{L})(\\b)".toRegex(), "$1$1$1$1$2")
    } else {
        // 50% chance to duplicate the first letter and add '-'
        if (first) input.replace("\\b(\\p{L})(\\p{L}*)\\b".toRegex(), "$1-$1$2") else input
    }

    return input + if (last) " ${PHRASES[stringLength % PHRASES.size]}" else ""
}
