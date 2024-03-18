package com.brody.arxiv.shared.settings.source

import android.util.Log
import androidx.datastore.core.DataStore
import com.brody.arxiv.shared.settings.data.SettingsPreferences
import com.brody.arxiv.shared.settings.data.QueryPreferences
import com.brody.arxiv.shared.settings.data.copy
import com.brody.arxiv.shared.settings.models.domain.QueryDataModel
import com.brody.arxiv.shared.settings.models.domain.SettingsDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

internal class SettingsDataSourceImpl @Inject constructor(
    private val queryPreferences: DataStore<QueryPreferences>,
    private val settingsPreferences: DataStore<SettingsPreferences>
) : SettingsDataSource {
    override val queryData: Flow<QueryDataModel>
        get() = queryPreferences.data.map {
            QueryDataModel(
                it.sortByValue,
                it.sortOrderValue,
                it.excludedIdsMap.keys
            )
        }.distinctUntilChanged()

    override val settingsData: Flow<SettingsDataModel>
        get() = settingsPreferences.data.map {
            SettingsDataModel(
                !it.isNotSaveSummary,
                !it.isNotSavePdfs
            )
        }.distinctUntilChanged()

    override suspend fun updateQuery(queryDataModel: QueryDataModel) {
        try {
            queryPreferences.updateData {
                it.copy {
                    sortByValue = queryDataModel.sortByOrdinal
                    sortOrderValue = queryDataModel.sortOrderOrdinal

                    excludedIds.clear()
                    excludedIds.putAll((queryDataModel.excludedIds ?: emptySet())
                        .associateWith { true })
                }
            }
        } catch (ioException: IOException) {
            Log.e("Settings", "Failed to update user preferences", ioException)
        }
    }

    override suspend fun updateSettings(settingsDataModel: SettingsDataModel) {
        try {
            settingsPreferences.updateData {
                it.copy {
                    isNotSaveSummary = !settingsDataModel.isSaveSummaries
                    isNotSavePdfs = !settingsDataModel.isSavePdfs
                }
            }
        } catch (ioException: IOException) {
            Log.e("Settings", "Failed to update settings preferences", ioException)
        }
    }
}