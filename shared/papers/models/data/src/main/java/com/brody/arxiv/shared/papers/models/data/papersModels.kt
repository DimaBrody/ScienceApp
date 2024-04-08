package com.brody.arxiv.shared.papers.models.data

import com.brody.arxiv.core.common.extensions.convertDateDefault
import com.brody.arxiv.shared.papers.models.domain.DomainAuthor
import com.brody.arxiv.shared.papers.models.domain.DomainCategory
import com.brody.arxiv.shared.papers.models.domain.DomainLink
import com.brody.arxiv.shared.papers.models.domain.PaperDomainModel
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("feed", "http://www.w3.org/2005/Atom")
data class ArxivResponse(
    val link: Link?,
    @XmlElement val title: String?,
    @XmlElement val id: String,
    @XmlElement val updated: String?,
//    @XmlElement @XmlSerialName("opensearch:totalResults") val totalResults: String?,
//    @XmlElement @XmlSerialName("opensearch:startIndex") val startIndex: String?,
//    @XmlElement @XmlSerialName("opensearch:itemsPerPage") val itemsPerPage: String?,
    val entry: List<Entry>?
)

@Serializable
@XmlSerialName("entry")
data class Entry(
    @XmlElement val id: String?,
    @XmlElement val updated: String?,
    @XmlElement val published: String?,
    @XmlElement val title: String?,
    @XmlElement val summary: String?,
    val author: List<Author>?,
    @XmlElement val doi: String?,
    val links: List<Link>?,
    @XmlElement val comment: String?,
    // Uncomment and apply XmlSerialName as needed
    // @XmlSerialName("arxiv:journal_ref") val journalRef: String?,
    // @XmlSerialName("arxiv:primary_category") val primaryCategory: Category?,
    val categories: List<Category>?
)

@Serializable
@XmlSerialName("link", "http://www.w3.org/2005/Atom")
data class Link(
    @XmlSerialName val title: String?,
    @XmlSerialName val href: String?,
    @XmlSerialName val rel: String?,
    @XmlSerialName val type: String?
)

@Serializable
@XmlSerialName("author")
data class Author(
    @XmlElement val name: String?
)

@Serializable
//@XmlSerialName("primary_category", "http://arxiv.org/schemas/atom", prefix = "arxiv")
@XmlSerialName("category")
data class Category(
    @XmlSerialName val term: String?
)

fun Entry.toDomainModel(subjectNames: SubjectNames) = PaperDomainModel(
    id = id.orEmpty(),
    updated = updated?.convertDateDefault(),
    published = published?.convertDateDefault(),
    title = title,
    authors = author?.map { DomainAuthor(it.name) },
    summary = summary,
    doi = doi,
    links = links?.map { DomainLink(it.title == "pdf", it.href) },
    comment = comment,
    categories = categories?.formatToCompleteCategories(subjectNames)?.map {
        DomainCategory(it.first, it.second)
    },
    hasSummaries = false
)

fun Entry.toEntityModel(subjectNames: SubjectNames) = PaperEntity(id = id.orEmpty(),
    updated = updated,
    published = published,
    title = title,
    summary = summary,
    author = author?.mapNotNull { it.name },
    doi = doi,
    links = links?.map { LinkEntry(it.title == "pdf", it.href) },
    comment = comment,
    categories = categories.formatToCompleteCategories(subjectNames)?.map {
        CategoryEntry(it.first, it.second)
    })

private fun List<Category>?.formatToCompleteCategories(subjectNames: SubjectNames): List<Pair<String, String>>? {
    return this?.mapNotNull { category ->
        val categoryValue = category.term ?: return@mapNotNull null

        val currentCategory = subjectNames[categoryValue] ?: return@mapNotNull null
        currentCategory to categoryValue
    }
}

typealias SubjectNames = HashMap<String, String>