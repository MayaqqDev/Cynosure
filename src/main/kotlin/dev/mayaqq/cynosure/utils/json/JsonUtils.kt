package dev.mayaqq.cynosure.utils.json

import kotlinx.serialization.json.*

public fun com.google.gson.JsonObject.toKotlinx(): JsonObject {
    return JsonObject(asMap().mapValues { (_, v) -> v.toKotlinx() })
}

public fun com.google.gson.JsonArray.toKotlinx(): JsonArray {
    return JsonArray(asList().map(com.google.gson.JsonElement::toKotlinx))
}

public fun com.google.gson.JsonPrimitive.toKotlinx(): JsonPrimitive {
    return when {
        isNumber -> JsonPrimitive(this.asNumber)
        isString -> JsonPrimitive(this.asShort)
        isBoolean -> JsonPrimitive(this.asBoolean)
        else -> JsonNull
    }
}

public fun com.google.gson.JsonElement.toKotlinx(): JsonElement = when(this) {
    is com.google.gson.JsonPrimitive -> toKotlinx()
    is com.google.gson.JsonObject -> toKotlinx()
    is com.google.gson.JsonArray -> toKotlinx()
    else -> JsonNull
}

public fun JsonElement.toGson(): com.google.gson.JsonElement = when(this) {
    JsonNull -> com.google.gson.JsonNull.INSTANCE
    is JsonPrimitive -> toGson()
    is JsonArray -> toGson()
    is JsonObject -> toGson()
}

public fun JsonObject.toGson(): com.google.gson.JsonObject {
    val gson = com.google.gson.JsonObject()
    this.mapValues { (_, v) -> v.toGson() }.forEach(gson::add)
    return gson
}

public fun JsonArray.toGson(): com.google.gson.JsonArray {
    val gson = com.google.gson.JsonArray()
    this.map(JsonElement::toGson).forEach(gson::add)
    return gson
}

public fun JsonPrimitive.toGson(): com.google.gson.JsonPrimitive {
    if (this == JsonNull) error("JsonNulls are not primitives in gson")
    return this.booleanOrNull?.let { com.google.gson.JsonPrimitive(it) }
        ?: this.intOrNull?.let { com.google.gson.JsonPrimitive(it) }
        ?: this.longOrNull?.let { com.google.gson.JsonPrimitive(it) }
        ?: this.floatOrNull?.let { com.google.gson.JsonPrimitive(it) }
        ?: this.doubleOrNull?.let { com.google.gson.JsonPrimitive(it) }
        ?: com.google.gson.JsonPrimitive(this.content)
}

@Suppress("UnusedReceiverParameter")
public fun JsonNull.toGson(): com.google.gson.JsonNull = com.google.gson.JsonNull.INSTANCE