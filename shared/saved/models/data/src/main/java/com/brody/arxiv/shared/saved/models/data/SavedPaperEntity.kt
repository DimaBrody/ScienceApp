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
    val links: List<SavedLinkEntry>,
    val comment: String,
    val categories: List<SavedCategoryEntry>,
    val isSaved: Boolean,
    val hasSummaries: Boolean
)

@Serializable
data class SavedCategoryEntry(
    val categoryName: String,
    val categoryId: String
)

@Serializable
data class SavedLinkEntry(
    val isPdf: Boolean,
    val href: String
)

fun SavedPaperEntity.toDomainModel() = PaperDomainModel(
    id = id,
    updated = updated,
    published = published,
    title = title,
    authors = author.map { DomainAuthor(it) },
    summary = summary,
    doi = doi,
    links = links.map { DomainLink(it.isPdf, it.href) },
    comment = comment,
    isSaved = isSaved,
    categories = categories.map {
        DomainCategory(it.categoryName, it.categoryId)
    },
    hasSummaries = hasSummaries
)

fun SaveablePaperDataModel.toEntityModel() = SavedPaperEntity(
    id = id,
    updated = updated,
    published = published,
    title = title,
    author = authors,
    summary = summary,
    doi = doi,
    links = links.map { SavedLinkEntry(it.isPdf, it.href) },
    comment = comment,
    categories = categories.map { SavedCategoryEntry(it.categoryName, it.categoryId) },
    isSaved = isSaved,
    hasSummaries = hasSummaries
)