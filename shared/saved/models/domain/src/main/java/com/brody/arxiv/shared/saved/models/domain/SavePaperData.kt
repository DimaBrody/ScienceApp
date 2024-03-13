package com.brody.arxiv.shared.saved.models.domain

import com.brody.arxiv.shared.papers.models.presentation.PaperUiModel

class SavePaperData(
    val id: String,
    val updated: String,
    val published: String,
    val title: String,
    val summary: String,
    val author: List<String>,
    val doi: String,
    val links: List<String>,
    val comment: String,
    val category: String,
    val categoryId: String
)

fun PaperUiModel.toSaveModel() = SavePaperData(
    id = id,
    updated = updated,
    published = published,
    title = title,
    summary = summary,
    author = authors,
    doi = doi,
    links = links,
    comment = comment,
    category = category,
    categoryId = categoryId
)