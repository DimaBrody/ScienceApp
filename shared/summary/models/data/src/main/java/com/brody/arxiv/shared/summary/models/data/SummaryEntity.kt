package com.brody.arxiv.shared.summary.models.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brody.arxiv.shared.summary.models.domain.SavedSummary
import com.brody.arxiv.shared.summary.models.domain.SummaryType

@Entity(tableName = "summaries_database")
data class SummaryEntity(
    @PrimaryKey val id: String,
    val summaries: Map<SummaryType, String>
)

fun SummaryEntity.toSavedSummary() =
    SavedSummary(
        id = id,
        summaries = summaries
    )

fun SavedSummary.toEntitySummary() =
    SummaryEntity(id = id, summaries = summaries)