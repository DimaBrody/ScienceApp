package com.brody.arxiv.features.papers.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brody.arxiv.core.threading.ArxivDispatchers
import com.brody.arxiv.core.threading.Dispatcher
import com.brody.arxiv.shared.papers.models.presentation.FetchPapers
import com.brody.arxiv.shared.settings.domain.usecases.GetQueryPrefUseCase
import com.brody.arxiv.shared.settings.domain.usecases.UpdateQueryPrefUseCase
import com.brody.arxiv.shared.settings.models.domain.QueryDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PapersViewModel @Inject constructor(
    getQueryPrefUseCase: GetQueryPrefUseCase,
    private val updateQueryPrefUseCase: UpdateQueryPrefUseCase,
    @Dispatcher(ArxivDispatchers.IO) private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    val uiState: StateFlow<PapersUiState> = getQueryPrefUseCase().map {
        PapersUiState.Result(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = PapersUiState.Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    fun updateQuery(saveDataModel: QueryDataModel) =
        viewModelScope.launch(coroutineDispatcher) {
            updateQueryPrefUseCase.invoke(saveDataModel)
        }

}

internal sealed interface PapersUiState {
    data object Loading : PapersUiState
    data class Result(val queryDataModel: QueryDataModel) : PapersUiState
}