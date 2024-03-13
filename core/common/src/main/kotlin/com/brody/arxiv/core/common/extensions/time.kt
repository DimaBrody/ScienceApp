package com.brody.arxiv.core.common.extensions

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun String.convertDateDefault(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Ensure the input is treated as UTC

    val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    outputFormat.timeZone = TimeZone.getDefault()

    val date = inputFormat.parse(this) ?: return ""

    return outputFormat.format(date)
}