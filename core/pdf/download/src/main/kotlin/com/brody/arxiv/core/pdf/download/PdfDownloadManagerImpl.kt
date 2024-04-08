package com.brody.arxiv.core.pdf.download

import android.app.DownloadManager
import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import com.brody.arxiv.core.pdf.download.utils.broadcastReceiverAsFlow
import com.brody.arxiv.core.pdf.download.utils.getDownloadPath
import com.brody.arxiv.core.pdf.download.utils.observerAsFlow
import com.brody.arxiv.core.threading.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject

internal class PdfDownloadManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val coroutineScope: CoroutineScope,
) : PdfDownloadManager {
    private val downloadManagerTitle = "Downloading Arxiv PDF"

    //    private val downloadManagerDescription = ""

    private val downloadObserver =
        context.contentResolver.observerAsFlow(Uri.parse("content://downloads/my_downloads"), true)

    private val downloadComplete =
        context.broadcastReceiverAsFlow(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
    private val getUriForFile = { downloadFile: File ->
        FileProvider.getUriForFile(context, "com.arxiv.brody" + ".fileprovider", downloadFile)
    }

    override val downloadState: MutableStateFlow<PdfDownloadState> =
        MutableStateFlow(PdfDownloadState.Idle)


    private fun setupDownloadObserver() {
        coroutineScope.launch {
            downloadObserver.collect {
                handleDownloadUpdate()
            }
        }
    }

    private fun handleDownloadUpdate() {
        coroutineScope.launch {
            val downloading = downloadState.value as? PdfDownloadState.Downloading ?: return@launch
            val downloadFileName = downloading.downloadFile.name
            val query = DownloadManager.Query()
            query.setFilterById(downloading.requestId)
            val state = downloadManager?.query(query)?.let { c ->
                var progress = 0.0f
                if (c.moveToFirst()) {
                    val sizeIndex: Int = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                    val downloadedIndex: Int =
                        c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                    val size = c.getInt(sizeIndex)
                    val downloaded = c.getInt(downloadedIndex)
                    if (size != -1) progress = downloaded.toFloat() / size
                }
                PdfDownloadState.Downloading(
                    downloading.requestId,
                    progress,
                    downloading.downloadFile,
                    downloadFileName
                )
            } ?: PdfDownloadState.Failed(downloadFileName)
            downloadState.update { state }
        }
    }

    private fun setupDownloadCompleteListener() {
        coroutineScope.launch {
            downloadComplete.collect {
                handleDownloadComplete()
            }
        }
    }

    private fun handleDownloadComplete() {
        coroutineScope.launch {
            val downloading = downloadState.value as? PdfDownloadState.Downloading
            val requestId = downloading?.requestId ?: return@launch
            val downloadFile = downloading.downloadFile
//            Log.d("HELLOD", downloadFile.toString())
            var success = false
            val query = DownloadManager.Query().apply {
                setFilterById(requestId)
            }
            downloadManager?.query(query)?.let { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    if (cursor.getInt(columnIndex) == DownloadManager.STATUS_SUCCESSFUL) {
                        success = true
                    }
                }
            }

            val downloadFileName = downloadFile.name
            if (success) {
                val outputUri = getUriForFile(downloadFile)
                downloadState.update { PdfDownloadState.Done(outputUri, downloadFileName) }
            } else {
                downloadState.update { PdfDownloadState.Failed(downloadFileName) }
            }
        }
    }

    override suspend fun startDownloading(url: String, fileName: String) {
        coroutineScope.launch(Dispatchers.Main) {

            val downloadFile = File(getDownloadPath(context), fileName)
            val requestId = downloadManager?.enqueue(DownloadManager.Request(Uri.parse(url)).apply {
                setTitle(downloadManagerTitle)
//                setDescription(downloadManagerDescription)
                setDestinationUri(Uri.fromFile(downloadFile))
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            })
            val stateToUpdate =
                requestId?.let {
                    PdfDownloadState.Downloading(
                        it,
                        0.0f,
                        downloadFile,
                        downloadFile.name
                    )
                }
                    ?: PdfDownloadState.Failed(fileName)

            downloadState.update { stateToUpdate }
        }
    }

    override fun isFileExists(fileName: String): Boolean {
        val file = createFullPathFile(fileName)

        return file.exists() && file.canRead()
    }

    override suspend fun isPdfCorrupted(fileName: String): Boolean = coroutineScope.async {
        if (!isFileExists(fileName)) {
            return@async true // File does not exist or cannot be read
        }

        val file = createFullPathFile(fileName)
        try {
            context.contentResolver.openFileDescriptor(Uri.fromFile(file), "r").use { pfd ->
                if (pfd != null) {
                    PdfRenderer(pfd).use { renderer ->
                        if (renderer.pageCount <= 0) {
                            return@async true // No pages in the PDF, indicating a potential problem
                        }
                    }
                } else {
                    return@async true // Failed to open PDF file descriptor
                }
            }
        } catch (e: IOException) {
            return@async true // IOException could indicate a corrupted file
        } catch (e: SecurityException) {
            return@async true // SecurityException might be thrown if there are issues accessing the file
        }
        return@async false // File seems to be a valid, readable PDF
    }.await()

    private fun createFullPathFile(fileName: String): File {
        val downloadPath = getDownloadPath(context)

        return File(downloadPath, fileName)
    }

    override fun getPdfUri(fileName: String): Uri {
        return getUriForFile(getPdfFile(fileName))
    }

    override fun getPdfFile(fileName: String): File {
        return File(getDownloadPath(context), fileName)
    }

    private val downloadManager: DownloadManager?
        get() = context.getSystemService<DownloadManager>()

    init {
        setupDownloadObserver()
        setupDownloadCompleteListener()
    }

}

