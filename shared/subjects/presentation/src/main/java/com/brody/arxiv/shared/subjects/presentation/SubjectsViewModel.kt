package com.brody.arxiv.shared.subjects.presentation

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brody.arxiv.core.threading.ArxivDispatchers
import com.brody.arxiv.core.threading.Dispatcher
import com.brody.arxiv.shared.subjects.domain.usecases.GetSubjectsWithSaved
import com.brody.arxiv.shared.subjects.models.presentation.ChipsData
import com.brody.arxiv.shared.subjects.models.presentation.SubjectChipData
import com.brody.arxiv.shared.subjects.models.presentation.SubjectsRequest
import com.brody.arxiv.shared.subjects.models.presentation.toChipsData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    getSubjectsWithSaved: GetSubjectsWithSaved,
    @Dispatcher(ArxivDispatchers.IO) private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val subjectsFlow = getSubjectsWithSaved()
    private var isFirstLoad = true


    private val subjectMap = hashMapOf<SubjectChipData, Boolean>()
    private var mapSnapshotNumber by mutableIntStateOf(0)

    private val requestValue = MutableStateFlow<SubjectsRequest>(SubjectsRequest.Waiting)
    private val responseValue: StateFlow<FlowState> =
        combine(subjectsFlow, requestValue) { subjects, subjectsRequest ->
            when (subjectsRequest) {
                is SubjectsRequest.Filters -> {
                    FlowState.Subjects(subjects.toChipsData(false, subjectsRequest.excludedIds))
                }

                is SubjectsRequest.Onboarding -> {
                    FlowState.Subjects(subjects.toChipsData(true))
                }

                else -> FlowState.Ignore
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            FlowState.Ignore
        )

    var uiState: StateFlow<SubjectsUiState> =
        merge(
            responseValue,
            snapshotFlow { mapSnapshotNumber }.map { FlowState.Map }
        ).map { flowState ->
            when (flowState) {
                is FlowState.Subjects -> {
                    subjectMap.clear()
                    subjectMap.putAll(flowState.subjects.data)

                    fillOnFirstLoad()
                    val output = subjectMap.entries.toList().map { it.toPair() }

                    when (flowState.subjects) {
                        is ChipsData.ChipsOnboarding -> SubjectsIntermediateState.FetchedOnboarding(
                            output
                        )

                        is ChipsData.ChipsFilter -> SubjectsIntermediateState.FetchedFilters(
                            output,
                            flowState.subjects.mapOfLinks
                        )
                    }
                }

                is FlowState.Map -> {
                    if (subjectMap.isEmpty()) return@map SubjectsIntermediateState.Waiting
                    fillOnFirstLoad()

                    val filtersMap =
                        ((responseValue.value as? FlowState.Subjects)?.subjects as? ChipsData.ChipsFilter)?.mapOfLinks

                    val output = subjectMap.entries.toList().map { it.toPair() }
                    if (filtersMap == null)
                        SubjectsIntermediateState.FetchedOnboarding(output)
                    else SubjectsIntermediateState.FetchedFilters(output, filtersMap)
                }

                else -> SubjectsIntermediateState.Waiting
            }
//
        }.map { intermediateState ->
            when (intermediateState) {
                is SubjectsIntermediateState.FetchedOnboarding -> {
                    SubjectsUiState.FetchedOnboarding(intermediateState.chips)
                }

                is SubjectsIntermediateState.FetchedFilters -> {
                    val hashMap = hashMapOf<String, MutableList<Pair<SubjectChipData, Boolean>>>()

                    val excludedIds = intermediateState.chips.filter { !it.second }.map { it.first }

                    intermediateState.chips.forEach {
                        val link = it.first.link
                        val name = intermediateState.mapOfLinks[link.subjectBits] ?: "Unknown"

                        if (name !in hashMap)
                            hashMap[name] = mutableListOf()

                        hashMap[name]?.add(it)
                    }


//                    Log.d("HELLOFILTER", excludedIds.toString())

                    SubjectsUiState.FetchedFilters(hashMap, excludedIds)
                }

                else -> SubjectsUiState.Waiting
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = SubjectsUiState.Waiting,
            started = SharingStarted.WhileSubscribed(5_000)
        )
        private set

    private fun fillOnFirstLoad() {
        if (isFirstLoad) {
            isFirstLoad = false
            subjectMap[subjectMap.keys.first()] = true
        }
    }

    fun requestSubjects(request: SubjectsRequest) {
        requestValue.update { request }
    }

    fun selectionChanged(chip: SubjectChipData, isSelected: Boolean) {
        subjectMap[chip] = isSelected
        mapSnapshotNumber++
    }
}

private sealed interface FlowState {
    data class Subjects(val subjects: ChipsData) : FlowState
    data object Map : FlowState

    data object Ignore : FlowState
}

sealed class SubjectsIntermediateState(
    open val chips: List<Pair<SubjectChipData, Boolean>>?
) {
    data object Waiting : SubjectsIntermediateState(null)

    class FetchedOnboarding(override val chips: List<Pair<SubjectChipData, Boolean>>) :
        SubjectsIntermediateState(chips)

    class FetchedFilters(
        override val chips: List<Pair<SubjectChipData, Boolean>>,
        val mapOfLinks: HashMap<Int, String>
    ) : SubjectsIntermediateState(chips)

}


@Immutable
sealed interface SubjectsUiState {
    data object Waiting : SubjectsUiState

    class FetchedOnboarding(val chips: List<Pair<SubjectChipData, Boolean>>) : SubjectsUiState

    class FetchedFilters(
        val chips: HashMap<String, MutableList<Pair<SubjectChipData, Boolean>>>,
        val excludedNodes: List<SubjectChipData> = emptyList()
    ) : SubjectsUiState

}
