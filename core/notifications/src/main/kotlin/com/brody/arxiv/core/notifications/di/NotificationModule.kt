package com.brody.arxiv.core.notifications.di

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.brody.arxiv.core.notifications.ArxivNotificationManager
import com.brody.arxiv.core.notifications.ArxivNotificationManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    companion object {
        @Provides
        fun providesNotificationCompat(@ApplicationContext context: Context): NotificationManagerCompat =
            NotificationManagerCompat.from(context)
    }

    @Binds
    abstract fun bindsNotificationManager(
        arxivNotificationManager: ArxivNotificationManagerImpl
    ): ArxivNotificationManager
}