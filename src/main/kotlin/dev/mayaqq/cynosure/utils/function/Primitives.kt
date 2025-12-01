package dev.mayaqq.cynosure.utils.function

public fun interface ToFloatFunction<T> {
    public operator fun invoke(t: T): Float
}

public fun interface FloatFunction<T> {
    public operator fun invoke(float: Float): T
}

public fun interface FloatOperator {
    public operator fun invoke(float: Float): Float
}

public fun interface ToDoubleFunction<T> {
    public operator fun invoke(t: T): Double
}

public fun interface DoubleFunction<T> {
    public operator fun invoke(double: Double): T
}

public fun interface DoubleOperater {
    public operator fun invoke(double: Double): Double
}