package dev.mayaqq.cynosure.utils.json

import kotlinx.serialization.json.*


@DslMarker
public annotation class JsonDSL1

@DslMarker
public annotation class JsonDSL2

public inline fun buildJson(builder: JsonBuilder.() -> Unit): JsonObject {
    val json = JsonBuilder()
    json.builder()
    return json.build()
}

public class JsonBuilder @PublishedApi internal constructor() {

    private val map: MutableMap<String, JsonElement> = mutableMapOf()

    @JsonDSL1
    public infix fun String.to(other: JsonElement?) {
        map[this] = other ?: JsonNull
    }

    @JsonDSL1
    public infix fun String.to(other: String?)  {
        this to JsonPrimitive(other)
    }

    @JsonDSL1
    public infix fun String.to(other: Number?) {
        this to JsonPrimitive(other)
    }

    @JsonDSL1
    public infix fun String.to(other: Boolean?) {
        this to JsonPrimitive(other)
    }

    @JsonDSL1
    public operator fun String.get(vararg elements: Any?) {
        this to array(*elements)
    }

    @JsonDSL1
    public inline operator fun String.invoke(builder: JsonBuilder.() -> Unit) {
        this to json(builder)
    }

    @JsonDSL2
    public inline fun json(builder: JsonBuilder.() -> Unit): JsonObject = buildJson(builder)

    @JsonDSL2
    public fun array(vararg elements: Any?): JsonArray = JsonArray(elements.map { when(it) {
        is JsonElement -> it
        is Number -> JsonPrimitive(it)
        is String -> JsonPrimitive(it)
        is Boolean -> JsonPrimitive(it)
        null -> JsonNull
        else -> TODO()
    } })

    @PublishedApi
    internal fun build(): JsonObject = JsonObject(map)

}