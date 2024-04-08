package com.brody.arxiv.core.pdf.download.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat

fun openPdfWithViewer(context: Context, outputUri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(outputUri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Very important to include
    }

    // Check if there's an app that can handle PDFs
    if (intent.resolveActivity(context.packageManager) != null) {
        ContextCompat.startActivity(context, intent, null)
    } else {
        // Handle case where no app can open PDFs
        // You might want to show a message to the user or direct them to download a PDF viewer app
        Toast.makeText(context, "You have no PDF viewers on phone", Toast.LENGTH_LONG).show()
    }
}