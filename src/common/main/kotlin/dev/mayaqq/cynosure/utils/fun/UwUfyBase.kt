@file:JvmName("UwUfy")
package dev.mayaqq.cynosure.utils.`fun`

import dev.mayaqq.cynosure.utils.`fun`.UwUfyBase.PHRASES

public fun String.uwufy(): String {
    var input = this
    val stringLength = input.length

    input = UwUfyBase.uwufyText(input)

    // Convert to uppercase
    if (stringLength % 3 == 0) {
        input = input.uppercase()
    }

    input = if (stringLength % 2 == 0) {
        // Add more letters to the end of words (Not numbers!)
        UwUfyBase.modifyLasts(input)
    } else {
        // 50% chance to duplicate the first letter and add '-'
        UwUfyBase.modifyFirsts(input)
    }

    return input + " " + PHRASES[stringLength % PHRASES.size]
}

public object UwUfyBase {

    public val PHRASES: Array<String> = arrayOf(
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

    public fun uwufyText(text: String): String {
        // Replace 'r' and 'l' with 'w', and 'R' and 'L' with 'W'
        // Replace 'ove' with 'uv' and 'OVE' with 'UV'
        // Replace 'o' with 'owo' and 'O' with 'OwO'
        // Replace repeated exclamation marks and question marks
        return text
            .replace("[rl]".toRegex(), "w").replace("[RL]".toRegex(), "W")
            .replace("ove".toRegex(), "uv").replace("OVE".toRegex(), "UV")
            .replace("o".toRegex(), "owo").replace("O".toRegex(), "OwO")
            .replace("!".toRegex(), "!!!").replace("\\?".toRegex(), "???")

            .replace(Regex("%(\\p{L})")) { m -> "%" + m.groupValues[1].lowercase() }
            .replace(Regex("\\$(\\p{L})")) { m -> "\\$" + m.groupValues[1].lowercase() }
    }

    public fun modifyLasts(text: String): String {
        return text.replace("(\\p{L})(\\b)".toRegex(), "$1$1$1$1$2")
    }

    public fun modifyFirsts(text: String): String {
        return text.replace("\\b(\\p{L})(\\p{L}*)\\b".toRegex(), "$1-$1$2")
    }
}