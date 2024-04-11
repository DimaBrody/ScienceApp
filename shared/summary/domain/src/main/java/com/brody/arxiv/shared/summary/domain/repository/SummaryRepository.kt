package com.brody.arxiv.shared.summary.domain.repository

import com.brody.arxiv.shared.summary.models.domain.SavedSummary
import com.brody.arxiv.shared.summary.models.domain.SettingsSummaryDataModel
import com.brody.arxiv.shared.summary.models.domain.SummaryLanguage
import com.brody.arxiv.shared.summary.models.domain.SummaryType
import com.langdroid.core.LangDroidModel
import com.langdroid.summary.SummaryState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface SummaryRepository {
    val settingsSummaryData: Flow<SettingsSummaryDataModel>

    fun getSummaryById(id: String): Flow<SavedSummary?>

    suspend fun saveSummary(type: SummaryType, summary: String, id: String)

    suspend fun updateSaveSummaries(isSaveSummaries: Boolean)

    suspend fun updateSummaryType(summaryType: SummaryType)

    suspend fun updateSummaryLanguage(language: SummaryLanguage)

    fun generateSummary(
        model: LangDroidModel<*>,
        isStream: Boolean,
        text: String,
        chunkPrompt: String?,
        finalPrompt: String?,
        systemMessage: String?
    ): SharedFlow<SummaryState>

}