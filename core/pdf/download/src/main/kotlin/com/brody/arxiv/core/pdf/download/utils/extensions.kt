package com.brody.arxiv.core.pdf.download.utils

import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun ContentResolver.observerAsFlow(
    uri: Uri, notifyForDecedents: Boolean, handler: Handler = Handler(
        Looper.getMainLooper()
    ), ignoreSelfChanges: Boolean = false
) = callbackFlow {
    val observer = object : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            if (ignoreSelfChanges && selfChange) return
            trySend(uri)
        }
    }
    registerContentObserver(uri, notifyForDecedents, observer)
    awaitClose {
        unregisterContentObserver(observer)
    }
}

fun Context.broadcastReceiverAsFlow(vararg actions: String) = callbackFlow {
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            trySend(intent)
        }
    }
    actions.forEach {
        registerReceiver(receiver, IntentFilter(it))
    }
    awaitClose {
        unregisterReceiver(receiver)
    }
}

fun String.pdfFileName() = if (!this.endsWith(".pdf", ignoreCase = true)) {
    "$this.pdf"
} else {
    this
}