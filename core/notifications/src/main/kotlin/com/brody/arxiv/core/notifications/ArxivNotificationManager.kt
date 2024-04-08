package com.brody.arxiv.core.notifications

import android.app.Notification
import android.app.PendingIntent
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel

interface ArxivNotificationManager {
    fun buildNotification(
        processedString: String,
        model: SaveablePaperDataModel,
        cancelIntent: PendingIntent
    ): ArxivNotification

    fun createNotificationChannel()

    fun cancelNotification(notificationId: Int)
}