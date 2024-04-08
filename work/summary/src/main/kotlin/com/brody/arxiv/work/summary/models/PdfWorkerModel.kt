package com.brody.arxiv.work.summary.models

import com.brody.arxiv.core.pdf.download.models.HasPdfModel
import com.brody.arxiv.core.pdf.download.models.PdfLink
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

data class PdfWorkerModel(override val links: ImmutableList<PdfLink>) : HasPdfModel()

fun SaveablePaperDataModel.toWorkerModel() = PdfWorkerModel(
    links = this.links.map { PdfLink(it.isPdf, it.href) }.toImmutableList()
)