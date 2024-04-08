package com.brody.arxiv.shared.summary.data.source

import android.util.Log
import androidx.datastore.core.DataStore
import com.brody.arxiv.shared.summary.data.SummaryPreferences
import com.brody.arxiv.shared.summary.data.copy
import com.brody.arxiv.shared.summary.data.database.SummaryDao
import com.brody.arxiv.shared.summary.models.data.SummaryEntity
import com.brody.arxiv.shared.summary.models.domain.SettingsSummaryDataModel
import com.brody.arxiv.shared.summary.models.domain.SummaryLanguage
import com.brody.arxiv.shared.summary.models.domain.SummaryType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class SummaryDataSourceImpl @Inject constructor(
    private val summaryPreferences: DataStore<SummaryPreferences>,
    private val summariesDao: SummaryDao
) : SummaryDataSource {
    override val settingsData: Flow<SettingsSummaryDataModel>
        get() = summaryPreferences.data.map {
            val language = it.language
            SettingsSummaryDataModel(
                isSaveSummaries = !it.isNotSaveSummary,
                summaryType = SummaryType.valueOf(it.summaryType.name),
                summaryLanguage = if (language.isNullOrEmpty())
                    SummaryLanguage.ENGLISH else SummaryLanguage.valueOf(language)
            )
        }.distinctUntilChanged()

    override suspend fun updateSaveSummaries(isSaveSummaries: Boolean) {
        summaryPreferences.updateData {
            it.copy {
                isNotSaveSummary = !isSaveSummaries
            }
        }
    }

    override suspend fun updateSummaryType(summaryType: SummaryType) {
        summaryPreferences.updateData {
            it.copy {
                this.summaryType =
                    com.brody.arxiv.shared.summary.data.SummaryType.valueOf(summaryType.name)
            }
        }
    }

    override suspend fun updateSummaryLanguage(language: SummaryLanguage) {
        summaryPreferences.updateData {
            it.copy { this.language = language.name }
        }
    }

    override fun getSummaryById(id: String): Flow<SummaryEntity?> =
        summariesDao.getSummaryById(id)


    override suspend fun saveSummary(type: SummaryType, summary: String, id: String) {
        val fetchedSummary = summariesDao.getSummaryById(id).firstOrNull()
//        Log.d("HELLOWSAVE1", "$fetchedSummary")
        val currentSummary = fetchedSummary ?: SummaryEntity(id, mutableMapOf())
//        Log.d("HELLOWSAVE2", "$fetchedSummary")

        // Step 2: Modify the summaries map with the new summary value for the specified type
        val updatedSummaries = currentSummary.summaries.toMutableMap().apply {
            put(type, summary)
        }
//        Log.d("HELLOWSAVE3", "$updatedSummaries")

        // Step 3: Save the updated entity back to the database
        summariesDao.insertSummary(currentSummary.copy(summaries = updatedSummaries))
//        Log.d("HELLOWSAVE4", "$updatedSummaries")
    }
}