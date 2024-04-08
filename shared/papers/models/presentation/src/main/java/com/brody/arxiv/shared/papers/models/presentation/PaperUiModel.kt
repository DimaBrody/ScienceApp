package com.brody.arxiv.shared.papers.models.presentation

import com.brody.arxiv.core.common.extensions.mapNotNullEmpty
import com.brody.arxiv.shared.papers.models.domain.DomainCategory
import com.brody.arxiv.shared.papers.models.domain.PaperDomainModel

data class PaperUiModel(
    val id: String,
    val updated: String,
    val published: String,
    val title: String,
    val summary: String,
    val authors: List<String>,
    val doi: String,
    val links: List<PaperUiLink>,
    val comment: String,
    val categories: List<PaperUiCategory>,
    val hasSummaries: Boolean,
    var isSaved: Boolean = false,
) {
    val primaryCategory: PaperUiCategory?
        get() = categories.firstOrNull()
}

data class PaperUiCategory(
    val categoryName: String,
    val categoryId: String
)

data class PaperUiLink(
    val isPdf: Boolean,
    val href: String
)

fun PaperDomainModel.toPresentationModel() = PaperUiModel(
    id = id,
    updated = updated.orEmpty(),
    published = published.orEmpty(),
    title = title.clean(),
    summary = summary.clean(),
    authors = authors.mapNotNullEmpty { it.name },
    doi = doi.orEmpty(),
    links = links.mapNotNullEmpty { it.href?.let { href -> PaperUiLink(it.isPdf, href) } },
    comment = comment.orEmpty(),
    categories = categories?.map { PaperUiCategory(it.categoryName, it.categoryId) }.orEmpty(),
    isSaved = isSaved,
    hasSummaries = hasSummaries
)

private fun String?.clean() = orEmpty()
    .trim() // Remove leading and trailing whitespace
    .replace("\n", "") // Remove newlines
    .replace(Regex("\\s+"), " ")