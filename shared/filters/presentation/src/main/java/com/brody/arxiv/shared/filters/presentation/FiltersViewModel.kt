package com.brody.arxiv.shared.filters.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brody.arxiv.shared.settings.general.models.domain.QueryDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class FiltersViewModel @Inject constructor(
) : ViewModel() {
    private var currentQueryDataModel: QueryDataModel? = null

    private val lastQueryDataModel = MutableStateFlow<QueryDataModel?>(null)

    val uiState: StateFlow<FiltersUiState> = lastQueryDataModel.map { last ->
        if (last == null || currentQueryDataModel == last)
            FiltersUiState.NotUpdate(currentQueryDataModel)
        else FiltersUiState.Update(last)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        FiltersUiState.NotUpdate(currentQueryDataModel)
    )

    fun updateQuery(onQueryUpdate: QueryDataModel.() -> QueryDataModel) {
        viewModelScope.launch {
            lastQueryDataModel.update { queryDataModel ->
                queryDataModel?.onQueryUpdate()
            }
        }
    }

    fun restore() {
        currentQueryDataModel = null
        lastQueryDataModel.value = null
    }

    fun setInitial(
        queryDataModel: QueryDataModel?
    ) {
        if (currentQueryDataModel != queryDataModel) {
            currentQueryDataModel = queryDataModel
            lastQueryDataModel.update {
                currentQueryDataModel
            }
        }
    }

}

internal sealed class FiltersUiState(
    open val queryDataModel: QueryDataModel?
) {
    data class NotUpdate(override val queryDataModel: QueryDataModel?) :
        FiltersUiState(queryDataModel)

    data class Update(override val queryDataModel: QueryDataModel) :
        FiltersUiState(queryDataModel)
}