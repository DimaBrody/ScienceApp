package com.brody.arxiv.shared.search.presentation.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brody.arxiv.core.common.properties.weak
import com.brody.arxiv.core.threading.ArxivDispatchers.IO
import com.brody.arxiv.core.threading.Dispatcher
import com.brody.arxiv.shared.search.models.presentation.SearchState
import com.brody.arxiv.shared.subjects.domain.usecases.GetSubjectsWithSaved
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    @Dispatcher(IO) private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val searchQueryFlow: MutableStateFlow<String> = MutableStateFlow("")
    private val isSearchActiveFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private var onSearchState: ((SearchState) -> Unit)?
            by weak(null) {
                val value = _searchStateFlow.value
                if (value != SearchState.Inactive)
                    it?.invoke(value)
            }

    init {
        viewModelScope.launch(coroutineDispatcher) {
            merge(
                searchQueryFlow.map { SearchState.Query(it) },
                isSearchActiveFlow.map {
                    if (it) SearchState.Query(searchQueryFlow.value)
                    else SearchState.Inactive
                }
            ).map { state ->
                onSearchState?.invoke(state)
                _searchStateFlow.update { state }
            }.collect()
        }
    }

    val isSearchActive: StateFlow<Boolean>
        get() = isSearchActiveFlow

    val searchQuery: StateFlow<String>
        get() = searchQueryFlow


    private val _searchStateFlow: MutableStateFlow<SearchState> =
        MutableStateFlow(SearchState.Inactive)

    val onSearchStateFlow: () -> StateFlow<SearchState>
        get() = { _searchStateFlow }

    fun onSearchQueryChange(newQuery: String) {
        this.searchQueryFlow.update { newQuery }
    }

    fun handleSearchClose() {
        if (searchQueryFlow.value.isNotEmpty())
            onSearchQueryChange("")
        else updateActive(false)
    }

    fun updateActive(isActive: Boolean, text: String? = null) {
        text?.let { searchQueryFlow.update { it } }
        this.isSearchActiveFlow.update { isActive }
    }

    fun connectToSearchState(onSearchState: (SearchState) -> Unit) {
        this.onSearchState = onSearchState
    }
}



