package com.brody.arxiv.shared.summary.data.repository

import com.brody.arxiv.shared.summary.data.di.SummaryChainFactory
import com.brody.arxiv.shared.summary.data.source.SummaryDataSource
import com.brody.arxiv.shared.summary.domain.repository.SummaryRepository
import com.brody.arxiv.shared.summary.models.data.toSavedSummary
import com.brody.arxiv.shared.summary.models.domain.SavedSummary
import com.brody.arxiv.shared.summary.models.domain.SettingsSummaryDataModel
import com.brody.arxiv.shared.summary.models.domain.SummaryLanguage
import com.brody.arxiv.shared.summary.models.domain.SummaryType
import com.langdroid.core.LangDroidModel
import com.langdroid.summary.SummaryState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class SummaryRepositoryImpl @Inject constructor(
    private val summaryChainFactory: SummaryChainFactory,
    private val summaryDataSource: SummaryDataSource,
) : SummaryRepository {

    override val settingsSummaryData: Flow<SettingsSummaryDataModel>
        get() = summaryDataSource.settingsData

    override fun getSummaryById(id: String): Flow<SavedSummary?> =
        summaryDataSource.getSummaryById(id).map { it?.toSavedSummary() }

    override suspend fun saveSummary(type: SummaryType, summary: String, id: String) {
        summaryDataSource.saveSummary(type, summary, id)
    }

    override suspend fun updateSaveSummaries(isSaveSummaries: Boolean) {
        summaryDataSource.updateSaveSummaries(isSaveSummaries)
    }

    override suspend fun updateSummaryType(summaryType: SummaryType) {
        summaryDataSource.updateSummaryType(summaryType)
    }

    override suspend fun updateSummaryLanguage(language: SummaryLanguage) {
        summaryDataSource.updateSummaryLanguage(language)
    }

    override fun generateSummary(
        model: LangDroidModel<*>,
        isStream: Boolean,
        text: String,
        chunkPrompt: String?,
        finalPrompt: String?,
        systemMessage: String?
    ): SharedFlow<SummaryState> {
        val summaryChain =
            summaryChainFactory.create(model, isStream, chunkPrompt, finalPrompt, systemMessage)

        return summaryChain.invokeAndGetFlow(text)
    }


}