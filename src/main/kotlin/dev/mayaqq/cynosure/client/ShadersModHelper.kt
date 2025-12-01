package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.core.isModLoaded
import net.irisshaders.iris.api.v0.IrisApi
import java.lang.invoke.LambdaMetafactory
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.function.BooleanSupplier

public val isShaderPackInUse: Boolean
    get() {
        if (shaderPackInUseSupplier == null) shaderPackInUseSupplier = init()
        return shaderPackInUseSupplier!!.asBoolean
    }

private var shaderPackInUseSupplier: BooleanSupplier? = null

private fun init(): BooleanSupplier = when {
    // If Iris or Oculus is loaded, use their api
    isModLoaded("iris") || isModLoaded("oculus") ->
        BooleanSupplier { IrisApi.getInstance().isShaderPackInUse }
    // If optifine is loaded, create a scary reflective accessor to its internals
    Package.getPackage("net.optifine.shaders") != null -> optifineShaderInUse()
    // Otherwise just always return false
    else -> BooleanSupplier { false }
}

private fun optifineShaderInUse(): BooleanSupplier {
    try {
        val ofShaders = Class.forName("net.optifine.shaders.Shaders")
        val field = ofShaders.getDeclaredField("shaderPackLoaded")
        field.isAccessible = true

        val lookup = MethodHandles.lookup()
        return LambdaMetafactory.metafactory(
            lookup,
            "getAsBoolean",
            MethodType.methodType(BooleanSupplier::class.java),
            MethodType.methodType(Boolean::class.javaPrimitiveType),
            lookup.unreflectGetter(field),
            MethodType.methodType(Boolean::class.javaPrimitiveType)
        ).target.invoke() as BooleanSupplier

    } catch (ignored: Exception) {
        return BooleanSupplier { false }
    }
}