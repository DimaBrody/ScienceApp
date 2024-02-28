package com.brody.arxiv.shared.search.presentation.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brody.arxiv.shared.search.models.presentation.SearchCategoriesNode
import com.brody.arxiv.shared.search.models.presentation.SearchSubjectsHierarchy
import com.brody.arxiv.shared.search.models.presentation.toPresentationModel
import com.brody.arxiv.shared.subjects.domain.usecases.GetSubjectDescriptions
import com.brody.arxiv.shared.subjects.domain.usecases.GetSubjectsWithSaved
import com.brody.arxiv.shared.subjects.domain.usecases.UpdateSubjectSaved
import com.brody.arxiv.shared.subjects.domain.usecases.UpdateSubjectsList
import com.brody.arxiv.shared.subjects.models.domain.SubjectDescriptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    getSubjectsWithSaved: GetSubjectsWithSaved,
    private val updateSubjectSaved: UpdateSubjectSaved,
    private val updateSubjectsList: UpdateSubjectsList,
    getSubjectDescriptions: GetSubjectDescriptions
) : ViewModel() {
    private val subjectsFlow = getSubjectsWithSaved()

    var searchQuery by mutableStateOf("")
        private set
    private val searchFlow = snapshotFlow { searchQuery }

    var isSearchActive by mutableStateOf(false)
        private set
    private val isSearchActiveFlow = snapshotFlow { isSearchActive }

    private var bottomSheetSubject by mutableStateOf<SearchCategoriesNode?>(null)
    private val bottomSheetSubjectFlow = snapshotFlow { bottomSheetSubject }

    var pagerExpandedTitle by mutableStateOf<String?>(null)

    private var latestSubjectsSum: Int = 0
    private var isUpdateRequired = false
    var prevExpandedTitle: String? = null

    val subjectsState: StateFlow<SearchSubjectsUiState> = combine(
        subjectsFlow, searchFlow, isSearchActiveFlow
    ) { subjects, query, isActive ->
        if (isUpdateRequired) {
            isUpdateRequired = false
            if (subjects.getSelectedIds().sum() != latestSubjectsSum)
                SearchSubjectsUiState.Inactive
        }

        if (isActive) {
            val queriedSubjects = subjects.toPresentationModel(query)

            SearchSubjectsUiState.Active(queriedSubjects)
        } else SearchSubjectsUiState.Inactive

    }.stateIn(
        scope = viewModelScope,
        initialValue = SearchSubjectsUiState.Inactive,
        started = SharingStarted.WhileSubscribed(5_000)
    )


    val sheetUiState = combine(
        bottomSheetSubjectFlow,
        getSubjectDescriptions()
    ) { subject, subjectDescriptions ->
        if (subject != null) {
            BottomSheetUiState.Active(BottomSheetData.createFromNode(subject, subjectDescriptions))
        } else BottomSheetUiState.Inactive
    }.stateIn(
        scope = viewModelScope,
        initialValue = BottomSheetUiState.Inactive,
        started = SharingStarted.WhileSubscribed(5_000)
    )


    fun onSearchQueryChange(newQuery: String) {
        this.searchQuery = newQuery
    }

    fun handleSearchClose() {
        if (searchQuery.isNotEmpty())
            onSearchQueryChange("")
        else updateActive(false)
    }

    fun updateActive(isActive: Boolean, chips: List<Int>? = null) = viewModelScope.launch {
        if (!isActive && !pagerExpandedTitle.isNullOrEmpty())
            updatePagerExpanded(null)

        setUpdateRequired(chips)
        this@SearchViewModel.isSearchActive = isActive

        chips?.let { updateSubjectsList.invoke(chips) }
    }

    private fun setUpdateRequired(chips: List<Int>? = null) {
        val chipsSum = chips?.sum() ?: 0
        isUpdateRequired = chipsSum != latestSubjectsSum
        latestSubjectsSum = chipsSum
    }

    fun toggleSelection(node: SearchCategoriesNode) = viewModelScope.launch {
        updateSelection(node, !node.isSelected)
    }

    private fun updateSelection(node: SearchCategoriesNode, isSelected: Boolean) =
        viewModelScope.launch {
            updateSubjectSaved(subjectsFlow.first(), node.link, isSelected)
        }

    fun openBottomSheet(node: SearchCategoriesNode) {
        bottomSheetSubject = node
    }

    fun dismissBottomSheet() {
        bottomSheetSubject = null
    }

    fun closePagerExpanded(isFirstPage: Boolean){
        if(isFirstPage) updatePagerExpanded(null)
        else updatePagerExpanded(prevExpandedTitle)
    }

    fun updatePagerExpanded(pagerExpandedTitle: String?) {
        prevExpandedTitle = this.pagerExpandedTitle
        this.pagerExpandedTitle = pagerExpandedTitle
    }
}


sealed class SearchSubjectsUiState(
    open val updatedMap: SearchSubjectsHierarchy? = null
) {
    data object Inactive : SearchSubjectsUiState()

    data class Active(override val updatedMap: SearchSubjectsHierarchy) :
        SearchSubjectsUiState(updatedMap)
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
        fun createFromNode(node: SearchCategoriesNode, descriptions: SubjectDescriptions) =
            BottomSheetData(
                node.name,
                node.id,
                descriptions[node.id] ?: "Subcategory"
            )
    }
}