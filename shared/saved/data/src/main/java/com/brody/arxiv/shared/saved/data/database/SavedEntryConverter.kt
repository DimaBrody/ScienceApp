package com.brody.arxiv.shared.saved.data.database

import androidx.room.TypeConverter
import com.brody.arxiv.shared.saved.models.data.SavedCategoryEntry
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SavedEntryConverter {
    private val json = Json { ignoreUnknownKeys = true } // Configure Json instance as needed

    @TypeConverter
    fun fromStringList(authors: List<String>): String = json.encodeToString(authors)

    @TypeConverter
    fun toStringList(authorString: String): List<String> = json.decodeFromString(authorString)

    @TypeConverter
    fun fromCategoriesList(categories: List<SavedCategoryEntry>): String =
        json.encodeToString(categories)

    @TypeConverter
    fun toCategoriesList(categoriesString: String): List<SavedCategoryEntry> =
        json.decodeFromString(categoriesString)

}
