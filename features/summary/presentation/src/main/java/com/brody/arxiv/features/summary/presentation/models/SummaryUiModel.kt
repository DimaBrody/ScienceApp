package com.brody.arxiv.features.summary.presentation.models

import androidx.compose.runtime.Stable
import com.brody.arxiv.core.pdf.download.models.HasPdfModel
import com.brody.arxiv.core.pdf.download.models.PdfLink
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Stable
data class SummaryUiModel(
    val id: String,
    val title: String,
    val isSaved: Boolean,
    override val links: ImmutableList<PdfLink>
) : HasPdfModel()

fun SaveablePaperDataModel?.toPresentationModel() = SummaryUiModel(
    id = this?.id.orEmpty(),
    title = this?.title.orEmpty(),
    isSaved = this?.isSaved == true,
    links = this?.links?.map { PdfLink(it.isPdf, it.href) }?.toImmutableList()
        ?: persistentListOf()
)