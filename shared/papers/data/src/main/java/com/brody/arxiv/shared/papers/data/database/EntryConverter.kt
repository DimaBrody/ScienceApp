package com.brody.arxiv.shared.papers.data.database

import androidx.room.TypeConverter
import com.brody.arxiv.shared.papers.models.data.Author
import com.brody.arxiv.shared.papers.models.data.CategoryEntry
import com.brody.arxiv.shared.papers.models.data.Link
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EntryConverter {
    private val json = Json { ignoreUnknownKeys = true } // Configure Json instance as needed

    @TypeConverter
    fun fromStringList(authors: List<String>): String = json.encodeToString(authors)

    @TypeConverter
    fun toStringList(authorString: String): List<String> = json.decodeFromString(authorString)

    @TypeConverter
    fun fromCategoriesList(categories: List<CategoryEntry>): String =
        json.encodeToString(categories)

    @TypeConverter
    fun toCategoriesList(categoriesString: String): List<CategoryEntry> =
        json.decodeFromString(categoriesString)
}
