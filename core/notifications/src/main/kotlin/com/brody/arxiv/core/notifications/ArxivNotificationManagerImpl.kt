package com.brody.arxiv.core.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.brody.arxiv.core.common.utils.createIdFromString
import com.brody.arxiv.designsystem.R
import com.brody.arxiv.features.summary.navigation.createNavLink
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ArxivNotificationManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManagerCompat
) : ArxivNotificationManager {

    @SuppressLint("MissingPermission")
    override fun buildNotification(
        processedString: String,
        model: SaveablePaperDataModel,
        cancelIntent: PendingIntent
    ): ArxivNotification {
        val deepLinkUri = Uri.parse(createNavLink(model, true))
        val extrasIntent = Intent(
            Intent.ACTION_VIEW,
            deepLinkUri,
            context,
            javaClass
        )

        val requestId = createIdFromString(model.id, model.summary)

        val isNotSticky = processedString.contains("finished")
                || processedString.contains("wrong")

        val pendingIntent = PendingIntent.getActivity(
            context, requestId, // Use unique requestCode for PendingIntent to ensure uniqueness
            extrasIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Use requestId to create a unique NOTIFICATION_ID for each request
        val uniqueNotificationId = NOTIFICATION_BASE_ID + requestId
//        if (ActivityCompat.checkSelfPermission(
//                context, Manifest.permission.POST_NOTIFICATIONS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }

        val cancelText = "Cancel summarization"
//        notificationManager.notify(uniqueNotificationId, notification)
        return ArxivNotification(
            uniqueNotificationId,
            NotificationCompat.Builder(context, CHANNEL_ID).setContentTitle(model.title)
                .setContentText(processedString) // Dynamic content based on state
                .setSmallIcon(R.drawable.arxiv_logo_vec)
                .setAutoCancel(isNotSticky) // Auto-cancel unless it's sticky
                .setOngoing(!isNotSticky)
                .addAction(android.R.drawable.ic_delete, cancelText, cancelIntent)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).build()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun createNotificationChannel() {
        val importance =
            NotificationManager.IMPORTANCE_DEFAULT

        val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = CHANNEL_DESC
        }

        notificationManager.createNotificationChannel(notificationChannel)
    }

    override fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    // Define a base ID for your notifications
    companion object {
        private const val NOTIFICATION_BASE_ID = 1000 // Just an example; adjust as needed
        private const val CHANNEL_ID = "arxiv_id"
        private const val CHANNEL_NAME = "Arxiv"
        private const val CHANNEL_DESC = "Arxiv App Notifications"
    }
}