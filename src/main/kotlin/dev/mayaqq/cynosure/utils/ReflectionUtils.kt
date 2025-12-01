package dev.mayaqq.cynosure.utils

public fun Any.hasField(fieldName: String): Boolean {
    try {
        javaClass.getDeclaredField(fieldName)
        return true
    } catch (e: Exception) {
        return false
    }
}