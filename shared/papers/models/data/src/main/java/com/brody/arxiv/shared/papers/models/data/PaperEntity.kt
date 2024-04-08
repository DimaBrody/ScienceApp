package com.brody.arxiv.shared.papers.models.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brody.arxiv.core.common.extensions.convertDateDefault
import com.brody.arxiv.shared.papers.models.domain.DomainAuthor
import com.brody.arxiv.shared.papers.models.domain.DomainCategory
import com.brody.arxiv.shared.papers.models.domain.DomainLink
import com.brody.arxiv.shared.papers.models.domain.PaperDomainModel
import kotlinx.serialization.Serializable

@Entity(tableName = "offline_papers")
data class PaperEntity(
    @PrimaryKey
    val id: String,
    val updated: String?,
    val published: String?,
    val title: String?,
    val summary: String?,
    val author: List<String>?,
    val doi: String?,
    val links: List<LinkEntry>?,
    val comment: String?,
    val categories: List<CategoryEntry>?,
)

@Serializable
data class CategoryEntry(
    val categoryName: String,
    val categoryId: String
)

@Serializable
data class LinkEntry(
    val isPdf: Boolean,
    val href: String?
)


fun PaperEntity.toDomainModel() = PaperDomainModel(
    id = id,
    updated = updated?.convertDateDefault(),
    published = published?.convertDateDefault(),
    title = title,
    authors = author?.map { DomainAuthor(it) },
    summary = summary,
    doi = doi,
    links = links?.map { DomainLink(it.isPdf, it.href) },
    comment = comment,
    categories = categories?.map {
        DomainCategory(
            categoryName = it.categoryName,
            categoryId = it.categoryId
        )
    },
    hasSummaries = false
)