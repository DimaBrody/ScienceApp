package com.brody.arxiv.shared.papers.models.domain

data class PaperDomainModel(
    val id: String,
    val updated: String?,
    val published: String?,
    val title: String?,
    val summary: String?,
    val authors: List<DomainAuthor>?,
    val doi: String?,
    val links: List<DomainLink>?,
    val comment: String?,
    val categories: List<DomainCategory>?,
    var isSaved: Boolean = false,
)
data class DomainAuthor(
    val name: String?
)

data class DomainLink(
    val href: String?
)

data class DomainCategory(
    val categoryName: String,
    val categoryId: String
)
