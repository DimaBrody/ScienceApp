package com.brody.arxiv.shared.saved.models.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brody.arxiv.shared.papers.models.domain.DomainAuthor
import com.brody.arxiv.shared.papers.models.domain.DomainCategory
import com.brody.arxiv.shared.papers.models.domain.DomainLink
import com.brody.arxiv.shared.papers.models.domain.PaperDomainModel
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import kotlinx.serialization.Serializable

@Entity(tableName = "saved_papers")
data class SavedPaperEntity(
    @PrimaryKey val id: String,
    val updated: String,
    val published: String,
    val title: String,
    val summary: String,
    val author: List<String>,
    val doi: String,
    val links: List<String>,
    val comment: String,
    val categories: List<SavedCategoryEntry>
)

@Serializable
data class SavedCategoryEntry(
    val categoryName: String,
    val categoryId: String
)

fun SavedPaperEntity.toDomainModel() = PaperDomainModel(
    id = id,
    updated = updated,
    published = published,
    title = title,
    authors = author.map { DomainAuthor(it) },
    summary = summary,
    doi = doi,
    links = links.map { DomainLink(it) },
    comment = comment,
    isSaved = true,
    categories = categories.map {
        DomainCategory(it.categoryName, it.categoryId)
    }
)

fun SaveablePaperDataModel.toEntityModel() = SavedPaperEntity(
    id = id,
    updated = updated,
    published = published,
    title = title,
    author = authors,
    summary = summary,
    doi = doi,
    links = links,
    comment = comment,
    categories = categories.map { SavedCategoryEntry(it.categoryName, it.categoryId) }
)