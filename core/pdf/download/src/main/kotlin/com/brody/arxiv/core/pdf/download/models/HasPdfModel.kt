package com.brody.arxiv.core.pdf.download.models

import com.brody.arxiv.core.pdf.download.utils.pdfFileName
import kotlinx.collections.immutable.ImmutableList

abstract class HasPdfModel {
    abstract val links: ImmutableList<PdfLink>

    val pdfLink: String?
        get() = links.firstOrNull { it.isPdf && it.href.isNotEmpty() }?.href

    val pdfName: String by lazy {
        val identifier = pdfLink?.substringAfterLast('/')
        identifier?.pdfFileName().orEmpty()
    }
}

data class PdfLink(
    val isPdf: Boolean,
    val href: String
)
