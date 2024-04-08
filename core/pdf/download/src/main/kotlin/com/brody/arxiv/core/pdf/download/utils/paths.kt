package com.brody.arxiv.core.pdf.download.utils

import android.content.Context
import java.io.File

internal fun getDownloadPath(context: Context) = File(
    context.externalCacheDir?.absolutePath,
    "pdfs"
).apply {
    mkdirs()
}