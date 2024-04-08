package com.brody.arxiv.core.pdf.download

import android.net.Uri
import java.io.File

sealed class PdfDownloadState(
    open val downloadFileName: String
) {
    data object Idle :
        PdfDownloadState("")

    data class Downloading(
        val requestId: Long,
        val progress: Float,
        val downloadFile: File,
        override val downloadFileName: String
    ) : PdfDownloadState(downloadFileName)

    data class Done(val outputUri: Uri, override val downloadFileName: String) :
        PdfDownloadState(downloadFileName)

    data class Failed(override val downloadFileName: String) :
        PdfDownloadState(downloadFileName)
}