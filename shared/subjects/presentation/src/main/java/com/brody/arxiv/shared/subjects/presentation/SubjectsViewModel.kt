package com.brody.arxiv.shared.subjects.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brody.arxiv.shared.subjects.domain.usecases.GetSubjectsWithSaved
import com.brody.arxiv.shared.subjects.domain.usecases.UpdateSubjectSaved
import com.brody.arxiv.shared.subjects.models.presentation.SubjectChipData
import com.brody.arxiv.shared.subjects.models.presentation.toChipsData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    getSubjectsWithSaved: GetSubjectsWithSaved
) : ViewModel() {

    private val subjectsFlow = getSubjectsWithSaved()
    private var isFirstLoad = true


    private val subjectMap = hashMapOf<SubjectChipData, Boolean>()
    private var mapSnapshotNumber by mutableIntStateOf(0)

    var uiState: StateFlow<SubjectsUiState> =
        merge(subjectsFlow.map {
            FlowState.Subjects(it.toChipsData(true))
        }, snapshotFlow { mapSnapshotNumber }.map { FlowState.Map }).map { flowState ->
            when (flowState) {
                is FlowState.Subjects -> {
                    subjectMap.clear()
                    subjectMap.putAll(flowState.subjects)
                    fillOnFirstLoad()

                    SubjectsUiState.Fetched(subjectMap.entries.toList().map { it.toPair() })
                }

                is FlowState.Map -> {
                    if (subjectMap.isEmpty()) return@map SubjectsUiState.Loading
                    fillOnFirstLoad()

                    SubjectsUiState.Fetched(subjectMap.entries.toList().map { it.toPair() })
                }

                else -> SubjectsUiState.Loading
            }
//
        }.stateIn(
            scope = viewModelScope,
            initialValue = SubjectsUiState.Loading,
            started = SharingStarted.WhileSubscribed(5_000)
        )
        private set

    private fun fillOnFirstLoad() {
        if (isFirstLoad) {
            isFirstLoad = false
            subjectMap[subjectMap.keys.first()] = true
        }
    }

    fun selectionChanged(chip: SubjectChipData, isSelected: Boolean) {
        subjectMap[chip] = isSelected
        mapSnapshotNumber++
    }
}

private sealed interface FlowState {
    data class Subjects(val subjects: List<Pair<SubjectChipData, Boolean>>)
    data object Map
}

sealed interface SubjectsUiState {
    data object Loading : SubjectsUiState

    data class Fetched(val chips: List<Pair<SubjectChipData, Boolean>>) : SubjectsUiState
}
