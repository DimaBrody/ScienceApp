package com.brody.arxiv.shared.saved.models.domain

import com.brody.arxiv.shared.papers.models.presentation.PaperUiModel
import kotlinx.serialization.Serializable

@Serializable
data class SaveablePaperDataModel(
    val id: String,
    val updated: String,
    val published: String,
    val title: String,
    val summary: String,
    val authors: List<String>,
    val doi: String,
    val links: List<SaveableLink>,
    val comment: String,
    val categories: List<SaveableCategory>,
    val isSaved: Boolean = false,
    var hasSummaries: Boolean
)

@Serializable
class SaveableCategory(
    val categoryName: String,
    val categoryId: String
)

@Serializable
class SaveableLink(
    val isPdf: Boolean,
    val href: String
)

fun PaperUiModel.toSaveableModel() = SaveablePaperDataModel(
    id = id,
    updated = updated,
    published = published,
    title = title,
    summary = summary,
    authors = authors,
    doi = doi,
    links = links.map { SaveableLink(it.isPdf, it.href) },
    comment = comment,
    categories = categories.map {
        SaveableCategory(it.categoryName, it.categoryId)
    },
    isSaved = isSaved,
    hasSummaries = hasSummaries
)

typealias OnPaperClicked = (SaveablePaperDataModel) -> Unit