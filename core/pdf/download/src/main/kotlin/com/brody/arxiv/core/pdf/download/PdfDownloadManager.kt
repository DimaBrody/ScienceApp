package com.brody.arxiv.core.pdf.download

import android.net.Uri
import kotlinx.coroutines.flow.SharedFlow
import java.io.File

interface PdfDownloadManager {
    val downloadState: SharedFlow<PdfDownloadState>

    suspend fun startDownloading(url: String, fileName: String)

    suspend fun isPdfCorrupted(fileName: String): Boolean

    fun isFileExists(fileName: String): Boolean

    fun getPdfUri(fileName: String): Uri

    fun getPdfFile(fileName: String): File
}


