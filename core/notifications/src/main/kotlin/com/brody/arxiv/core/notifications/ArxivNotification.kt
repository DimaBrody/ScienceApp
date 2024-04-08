package com.brody.arxiv.core.notifications

import android.app.Notification

data class ArxivNotification(
    val notificationId: Int,
    val notification: Notification
)