package com.brody.arxiv.shared.subjects.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SubjectsPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun updateSubjectsList(subjectsList: List<Int>) {
        val subjectsSet = subjectsList.toSet()

        dataStore.edit { preferences ->
            // Fetch and prepare current subjects from preferences for comparison
            val currentSubjects = preferences.asMap().entries.mapNotNull { entry ->
                entry.key.name.toIntOrNull()?.takeIf { it in subjectsSet || entry.value == true }
            }.toSet()

            // Add or confirm selection for subjects in the provided list
            subjectsSet.forEach { subjectId ->
                preferences[subjectKey(subjectId)] = true
            }

            // Remove unselected subjects that are not in the provided list
            currentSubjects.forEach { subjectId ->
                if (subjectId !in subjectsSet) {
                    preferences.remove(subjectKey(subjectId))
                }
            }
        }
    }

    private fun subjectKey(id: Int): Preferences.Key<Boolean> = booleanPreferencesKey(id.toString())

    suspend fun setupSubject(subjectId: Int, isSelected: Boolean) {
        val key = subjectKey(subjectId)
        dataStore.edit { preferences ->
            if (isSelected) {
                preferences[key] = true
            } else {
                // If not selected, remove the key to clean up the preferences
                preferences.remove(key)
            }
        }
    }

    val subjectsList: Flow<List<Int>?> = dataStore.data
        .map { preferences ->
            preferences.asMap().entries.mapNotNull { entry ->
                entry.key.name.toIntOrNull()
            }
        }

}