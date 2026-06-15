package dev.mayaqq.cynosure.core

public enum class TriState {
    TRUE,
    INTERMEDIATE,
    FALSE;

    public fun toBoolean(): Boolean = this == TRUE

    public fun toNullableBoolean(): Boolean? = when (this) {
        TRUE -> true
        FALSE -> false
        else -> null
    }

    public operator fun not(): TriState = when (this) {
        TRUE -> FALSE
        FALSE -> TRUE
        else -> this
    }

    public infix fun and(other: TriState): Boolean = this == TRUE && other == TRUE

    public infix fun or(other: TriState): Boolean = this == TRUE || other == TRUE
}

public fun Boolean?.toTriState(): TriState = when (this) {
    true -> TriState.TRUE
    false -> TriState.FALSE
    null -> TriState.INTERMEDIATE
}
