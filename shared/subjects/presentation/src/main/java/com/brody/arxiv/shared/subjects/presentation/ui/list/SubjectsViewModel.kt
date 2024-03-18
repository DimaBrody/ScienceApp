package com.brody.arxiv.shared.subjects.presentation.ui.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brody.arxiv.core.threading.ArxivDispatchers
import com.brody.arxiv.core.threading.Dispatcher
import com.brody.arxiv.shared.search.models.presentation.SearchState
import com.brody.arxiv.shared.subjects.domain.usecases.GetSubjectDescriptions
import com.brody.arxiv.shared.subjects.domain.usecases.GetSubjectsWithSaved
import com.brody.arxiv.shared.subjects.domain.usecases.UpdateSubjectSaved
import com.brody.arxiv.shared.subjects.domain.usecases.UpdateSubjectsList
import com.brody.arxiv.shared.subjects.models.domain.SubjectDescriptions
import com.brody.arxiv.shared.subjects.models.presentation.CategoriesNodeUiModel
import com.brody.arxiv.shared.subjects.models.presentation.SubjectsHierarchyUiModel
import com.brody.arxiv.shared.subjects.models.presentation.toPresentationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    getSubjectsWithSaved: GetSubjectsWithSaved,
    private val updateSubjectSaved: UpdateSubjectSaved,
    private val updateSubjectsList: UpdateSubjectsList,
    private val getSubjectDescriptions: GetSubjectDescriptions,
    @Dispatcher(ArxivDispatchers.IO) private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val subjectsFlow = getSubjectsWithSaved()

    private val bottomSheetSubjectFlow = MutableStateFlow<CategoriesNodeUiModel?>(null)

    // Overlay over search bar (Here, not in search module, because of much less code and no
    // large functionality difference between it and search
    var pagerExpandedTitle by mutableStateOf<String?>(null)
    var prevExpandedTitle: String? = null

    // Update from chips to list
    private val pageCountInput: () -> Int = { 3 }

    val pagerState: SubjectsPagerState = SubjectsPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        updatedPageCount = pageCountInput
    ).apply { pageCountState.value = pageCountInput }

    val sheetUiState: StateFlow<BottomSheetUiState> = combine(
        bottomSheetSubjectFlow,
        getSubjectDescriptions()
    ) { subject, subjectDescriptions ->
        if (subject != null) {
            BottomSheetUiState.Active(
                BottomSheetData.createFromNode(
                    subject,
                    subjectDescriptions
                )
            )
        } else BottomSheetUiState.Inactive
    }.stateIn(
        scope = viewModelScope,
        initialValue = BottomSheetUiState.Inactive,
        started = SharingStarted.WhileSubscribed(5_000)
    )


    private var latestSubjectsSum: Int = 0
    private var isUpdateRequired = false

    fun getSubjectsState(
        searchFlow: StateFlow<SearchState>,
        isAlwaysActive: Boolean = false
    ): StateFlow<SubjectsUiState> = combine(
        subjectsFlow, searchFlow
    ) { subjects, searchState ->
        updateFromActive(searchState !is SearchState.Inactive)

        if (isAlwaysActive || searchState is SearchState.Query) {
            val value = (searchState as? SearchState.Query)?.value.orEmpty()
            val queriedSubjects = subjects.toPresentationModel(value)

            SubjectsUiState.Active(queriedSubjects)
        } else SubjectsUiState.Waiting

    }.stateIn(
        scope = viewModelScope,
        initialValue = SubjectsUiState.Waiting,
        started = SharingStarted.WhileSubscribed(5_000)
    )

    private var onGetCurrentChips: WeakReference<() -> List<Int>?> =
        WeakReference(null)

    fun setOnGetCurrentChips(onGetCurrentChips: (() -> List<Int>)?) {
        this.onGetCurrentChips = WeakReference(onGetCurrentChips)
    }

    private fun updateFromActive(isActive: Boolean) =
        viewModelScope.launch(coroutineDispatcher) {
            if (!isActive && !pagerExpandedTitle.isNullOrEmpty())
                updatePagerExpanded(null)

            onUpdateRequired {
                val chips: List<Int>? = onGetCurrentChips.get()?.invoke()
                setUpdateRequired(chips)

                if (isUpdateRequired)
                    chips?.let { updateSubjectsList.invoke(chips) }
            }

        }

    private inline fun CoroutineScope.onUpdateRequired(
        crossinline callback: suspend () -> Unit
    ) {
        if (onGetCurrentChips.get() != null && isUpdateRequired) {
            launch { callback() }
        }
    }

    private fun setUpdateRequired(chips: List<Int>? = null) {
        val chipsSum = chips?.sum() ?: 0
        isUpdateRequired = chipsSum != latestSubjectsSum
        latestSubjectsSum = chipsSum
    }

    fun setUpdateRequired(isUpdateRequired: Boolean) {
        this.isUpdateRequired = isUpdateRequired
    }

    fun toggleSelection(node: CategoriesNodeUiModel) = viewModelScope.launch(coroutineDispatcher) {
        updateSelection(node, !node.isSelected)
    }

    private fun updateSelection(node: CategoriesNodeUiModel, isSelected: Boolean) =
        viewModelScope.launch(coroutineDispatcher) {
            updateSubjectSaved(subjectsFlow.first(), node.link, isSelected)
        }


    fun openBottomSheet(node: CategoriesNodeUiModel) {
        bottomSheetSubjectFlow.update { node }
    }

    fun dismissBottomSheet() {
        bottomSheetSubjectFlow.update { null }
    }

    fun closePagerExpanded(isFirstPage: Boolean) {
        if (isFirstPage) updatePagerExpanded(null)
        else updatePagerExpanded(prevExpandedTitle)
    }

    fun updatePagerExpanded(pagerExpandedTitle: String?) {
        prevExpandedTitle = this.pagerExpandedTitle
        this.pagerExpandedTitle = pagerExpandedTitle
    }
}


sealed interface BottomSheetUiState {
    data object Inactive : BottomSheetUiState
    data class Active(val data: BottomSheetData) : BottomSheetUiState
}

data class BottomSheetData(
    val name: String,
    val id: String,
    val desc: String
) {
    companion object {
        fun createFromNode(node: CategoriesNodeUiModel, descriptions: SubjectDescriptions) =
            BottomSheetData(
                node.name,
                node.id,
                descriptions[node.id] ?: "Subcategory"
            )
    }
}


sealed class SubjectsUiState(
    open val updatedMap: SubjectsHierarchyUiModel? = null
) {
    data object Waiting : SubjectsUiState()

    data class Active(override val updatedMap: SubjectsHierarchyUiModel) :
        SubjectsUiState(updatedMap)
}
