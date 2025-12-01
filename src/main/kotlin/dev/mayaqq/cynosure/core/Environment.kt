package dev.mayaqq.cynosure.core

public val currentEnvironment: Environment
    get() = PlatformHooks.environment

public enum class Environment {
    CLIENT, SERVER;
}

public inline val isClient: Boolean
    get() = currentEnvironment == Environment.CLIENT
public inline val isServer: Boolean
    get() = currentEnvironment == Environment.SERVER