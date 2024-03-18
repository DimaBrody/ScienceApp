package com.brody.arxiv.features.details.presentation.models

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.brody.arxiv.shared.papers.models.presentation.PaperUiModel
import com.brody.arxiv.shared.saved.models.domain.SaveableCategory
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList


@Stable
data class DetailsUiModel(
    val id: String,
    val updated: String,
    val published: String,
    val title: String,
    val summary: String,
    val authors: ImmutableList<String>,
    val doi: String,
    val links: ImmutableList<String>,
    val comment: String,
    val categories: ImmutableList<DetailsUiCategory>,
    val isConsistent: Boolean,
    var isSaved: Boolean = false,
)

data class DetailsUiCategory(
    val name: String, val id: String
)

fun SaveablePaperDataModel?.toPresentationModel() = DetailsUiModel(id = this?.id ?: "",
    updated = this?.updated ?: "",
    published = this?.published ?: "",
    title = this?.title ?: "No info",
    summary = this?.summary ?: "",
    authors = this?.authors?.toImmutableList() ?: persistentListOf(),
    doi = this?.doi ?: "",
    links = this?.links?.toImmutableList() ?: persistentListOf(),
    comment = this?.comment ?: "",
    categories = this?.categories?.map { DetailsUiCategory(it.categoryName, it.categoryId) }
        ?.toImmutableList() ?: persistentListOf(),
    isSaved = this?.isSaved ?: false,
    isConsistent = this != null)


fun DetailsUiModel.toSaveableModel() = SaveablePaperDataModel(
    id = id,
    updated = updated,
    published = published,
    title = title,
    summary = summary,
    authors = authors,
    doi = doi,
    links = links,
    comment = comment,
    categories = categories.map {
        SaveableCategory(it.name, it.id)
    },
    isSaved = isSaved
)
