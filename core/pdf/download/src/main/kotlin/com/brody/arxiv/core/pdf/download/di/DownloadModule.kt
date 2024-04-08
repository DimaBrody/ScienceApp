package com.brody.arxiv.core.pdf.download.di

import com.brody.arxiv.core.pdf.download.PdfDownloadManager
import com.brody.arxiv.core.pdf.download.PdfDownloadManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DownloadModule {
    @Binds
    internal abstract fun bindsDownloadMonitor(
        networkMonitor: PdfDownloadManagerImpl,
    ): PdfDownloadManager
}