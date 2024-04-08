package com.brody.arxiv.core.pdf.extract.di

import com.brody.arxiv.core.pdf.extract.PdfTextExtractor
import com.brody.arxiv.core.pdf.extract.extractors.SimplePdfTextExtractor
import com.brody.arxiv.core.pdf.extract.extractors.XmlPdfTextExtractor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ExtractModule {
    @Binds
    internal abstract fun bindsTextExtractor(
        networkMonitor: SimplePdfTextExtractor,
    ): PdfTextExtractor

    @Binds
    internal abstract fun bindsXmlTextExtractor(
        networkMonitor: XmlPdfTextExtractor,
    ): PdfTextExtractor
}