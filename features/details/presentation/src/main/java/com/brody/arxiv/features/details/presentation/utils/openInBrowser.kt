package com.brody.arxiv.features.details.presentation.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat

fun openUrlInCustomTab(context: Context, url: String) {
    val customTabsIntent = CustomTabsIntent.Builder().build()

    // Optionally, customize your Chrome Custom Tabs here

    // Checking if Chrome is available
    val packageName = customTabsIntent.intent.resolveActivity(context.packageManager)
    if (packageName == null) {
        // Chrome is not installed, fallback to using the default browser
        openUrlInBrowser(context, url)
    } else {
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }
}

private fun openUrlInBrowser(context: Context, url: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    ContextCompat.startActivity(context, browserIntent, null)
}