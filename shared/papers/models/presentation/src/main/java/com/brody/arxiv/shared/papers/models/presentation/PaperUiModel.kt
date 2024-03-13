package com.brody.arxiv.shared.papers.models.presentation

import com.brody.arxiv.core.common.extensions.mapNotNullEmpty
import com.brody.arxiv.shared.papers.models.domain.PaperDomainModel

data class PaperUiModel(
    val id: String,
    val updated: String,
    val published: String,
    val title: String,
    val summary: String,
    val authors: List<String>,
    val doi: String,
    val links: List<String>,
    val comment: String,
    val category: String,
    val categoryId: String,
    var isSaved: Boolean = false
)

fun PaperDomainModel.toPresentationModel() = PaperUiModel(
    id = id,
    updated = updated.orEmpty(),
    published = published.orEmpty(),
    title = title.orEmpty(),
    summary = summary.orEmpty().trim().replace("\n", ""),
    authors = authors.mapNotNullEmpty { it.name },
    doi = doi.orEmpty(),
    links = links.mapNotNullEmpty { it.href },
    comment = comment.orEmpty(),
    category = category.orEmpty(),
    categoryId = categoryId.orEmpty(),
    isSaved = isSaved
)