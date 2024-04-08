package com.brody.arxiv.shared.summary.data.database

import androidx.room.TypeConverter
import com.brody.arxiv.shared.summary.models.domain.SummaryType
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SummaryEntryConverter {
    private val json = Json { ignoreUnknownKeys = true } // Configure Json instance as needed

    @TypeConverter
    fun fromSummaryTypeMap(summaries: Map<SummaryType, String>): String {
        // Define the serializer for your map
        val serializer = MapSerializer(SummaryType.serializer(), String.serializer())
        return json.encodeToString(serializer, summaries)
    }

    @TypeConverter
    fun toSummaryTypeMap(summariesString: String): Map<SummaryType, String> {
        // Define the serializer for your map
        val serializer = MapSerializer(SummaryType.serializer(), String.serializer())
        return json.decodeFromString(serializer, summariesString)
    }

}
