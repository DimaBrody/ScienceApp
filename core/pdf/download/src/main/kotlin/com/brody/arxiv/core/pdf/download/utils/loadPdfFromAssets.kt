package com.brody.arxiv.core.pdf.download.utils

import android.content.Context
import java.io.File

fun loadPdfFromAssets(context: Context, assetFileName: String): File {
    context.assets.open(assetFileName).use { inputStream ->
        val outputFile = File(context.filesDir, assetFileName)
        outputFile.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }
        return outputFile
    }
}