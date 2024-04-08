package com.brody.arxiv.work.summary.converters

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

class WorkerSerializableConverter(
    public val json: Json
) {

    // Inline function with reified T to automatically obtain the serializer
    inline fun <reified T> serialize(obj: T): String {
        return json.encodeToString(serializer(), obj)
    }

    // Inline function with reified T to automatically obtain the serializer for deserialization
    inline fun <reified T> deserialize(string: String): T {
        return json.decodeFromString(serializer(), string)
    }
}