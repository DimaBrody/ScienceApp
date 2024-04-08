package com.brody.arxiv.core.pdf.extract

import java.io.File

interface PdfTextExtractor {
    suspend fun extract(pdfFile: File): String
}