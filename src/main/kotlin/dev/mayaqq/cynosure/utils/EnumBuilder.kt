@file:OptIn(ExperimentalTypeInference::class)

package dev.mayaqq.cynosure.utils

import dev.mayaqq.cynosure.utils.function.and
import dev.mayaqq.cynosure.utils.unsafe.*
import java.lang.invoke.MethodHandles
import java.lang.invoke.VarHandle
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.experimental.ExperimentalTypeInference
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

public inline fun <reified E : Enum<E>> createEnumConstant(id: String, @BuilderInference builder: EnumBuilder<E>.() -> Unit = {}): E {
    val b = EnumBuilder(E::class.java, id)
    b.builder()
    return b.build()
}

public inline fun <reified E : Enum<E>> enumConstant(@BuilderInference crossinline builder: EnumBuilder<E>.() -> Unit = {}): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, E>> {
    return PropertyDelegateProvider { _, property ->
        val constant = createEnumConstant(property.name, builder)
        ReadOnlyProperty { _, _ -> constant }
    }
}

private typealias Predicate<T> = (T) -> Boolean

@Suppress("unused")
public class EnumBuilder<T : Enum<T>>(private val enumClass: Class<T>, private val id: String) {
    private val args: MutableList<Any> = ArrayList()
    private val types: MutableList<Class<*>> = ArrayList()

    public fun arg(type: Class<*>, arg: Any): EnumBuilder<T> {
        args.add(arg)
        types.add(type)
        return this
    }

    public inline fun <reified A : Any> arg(arg: A): EnumBuilder<T> {
        return arg(A::class.java, arg)
    }

    @Throws(Throwable::class)
    public fun build(): T {
        val argTypes: Array<Class<*>?> = arrayOfNulls(types.size + 2)
        argTypes[0] = String::class.java
        argTypes[1] = Int::class.javaPrimitiveType!!
        System.arraycopy(types.toTypedArray(), 0, argTypes, 2, types.size)

        val newArgs = arrayOfNulls<Any>(args.size + 2)
        newArgs[0] = id
        newArgs[1] = enumClass.enumConstants.size
        System.arraycopy(args.toTypedArray(), 0, newArgs, 2, args.size)

        verify(argTypes, newArgs)

        return create(enumClass, newArgs, argTypes)
    }

    private fun verify(argTypes: Array<Class<*>?>, newArgs: Array<Any?>) {
        require(argTypes.size == newArgs.size) { "Argument types and arguments must be the same length" }
    }

    private companion object {
        private val TRUSTED_LOOKUP: MethodHandles.Lookup = MethodHandles.Lookup::class.java
            .getDeclaredField("IMPL_LOOKUP")
            .apply { isAccessible = true }
            .get(null) as MethodHandles.Lookup

        private val ENUM_CONSTANTS: VarHandle = TRUSTED_LOOKUP.unreflectVarHandle(
            getField<Class<*>> { it.name == "enumConstants" }
                .apply { isAccessible = true }
        )
        private val ENUM_CONSTANT_DIRECTORY: VarHandle = TRUSTED_LOOKUP.unreflectVarHandle(
            getField<Class<*>> { it.name == "enumConstantDirectory" }
                .apply { isAccessible = true }
        )

        private val IS_SYNTHETIC: Predicate<Field> = Field::isSynthetic
        private val IS_ARRAY: Predicate<Field> = { it.type.isArray }
        private val IS_STATIC: Predicate<Field> = { Modifier.isStatic(it.modifiers) }
        private val NAMED_VALUES: Predicate<Field> = { it.name.contains("VALUES") }
        private val IS_VALUES_FIELD: Predicate<Field> = IS_SYNTHETIC and IS_STATIC and IS_ARRAY and NAMED_VALUES

        //region Unsafe operations for enum creation
        @OptIn(CynosureUnsafeApi::class)
        @Throws(Throwable::class)
        private fun <T : Enum<T>> create(enumClass: Class<T>, args: Array<Any?>, types: Array<Class<*>?>): T {
            val constructor: Constructor<T> = enumClass.getDeclaredConstructor(*types)
            constructor.isAccessible = true
            val output = TRUSTED_LOOKUP.unreflectConstructor(constructor)
                .invokeWithArguments(*args) as T

            val valuesField = TRUSTED_LOOKUP.unreflectVarHandle(enumClass.getDeclaredField { IS_VALUES_FIELD(it) && it.type.componentType == enumClass }.apply { isAccessible = true })
            addArrayValue(valuesField, enumClass, output)
            ENUM_CONSTANTS.set(enumClass, null)
            ENUM_CONSTANT_DIRECTORY.set(enumClass, null)
            return output
        }

        @OptIn(CynosureUnsafeApi::class)
        private fun <T> addArrayValue(data: VarHandle, clazz: Class<T>, arrayEntry: T) {
//            val base = UNSAFE.staticFieldBase(data)
//            val offset = UNSAFE.staticFieldOffset(data)
            val array = data.get() as Array<T>
            val newArray = java.lang.reflect.Array.newInstance(clazz, array.size + 1) as Array<T>
            System.arraycopy(array, 0, newArray, 0, array.size)
            newArray[array.size] = arrayEntry
            data.set(newArray)
        } //endregion
    }
}