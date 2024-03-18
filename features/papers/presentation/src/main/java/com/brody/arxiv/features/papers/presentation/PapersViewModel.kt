package com.brody.arxiv.features.papers.presentation

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brody.arxiv.core.threading.ArxivDispatchers
import com.brody.arxiv.core.threading.Dispatcher
import com.brody.arxiv.shared.settings.domain.usecases.GetQueryPrefUseCase
import com.brody.arxiv.shared.settings.domain.usecases.UpdateQueryPrefUseCase
import com.brody.arxiv.shared.settings.models.domain.QueryDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PapersViewModel @Inject constructor(
    getQueryPrefUseCase: GetQueryPrefUseCase,
    private val updateQueryPrefUseCase: UpdateQueryPrefUseCase,
    @Dispatcher(ArxivDispatchers.IO) private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var snapshotIsOfflineValue by mutableStateOf(false)
    private val snapshotIsOfflineFlow = snapshotFlow { snapshotIsOfflineValue }

    val uiState: StateFlow<PapersUiState> = combine(
        snapshotIsOfflineFlow,
        getQueryPrefUseCase()
    ) { isOffline, queryData ->
        if (isOffline) PapersUiState.Offline
        else PapersUiState.Query(queryData)
    }.stateIn(
        scope = viewModelScope,
        initialValue = PapersUiState.Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    fun updateQuery(saveDataModel: QueryDataModel) = viewModelScope.launch(coroutineDispatcher) {
        updateQueryPrefUseCase.invoke(saveDataModel)
    }

    fun updateOfflineState(isOffline: Boolean) {
        snapshotIsOfflineValue = isOffline
    }

}

@Immutable
internal sealed interface PapersUiState {
    data object Loading : PapersUiState
    data class Query(val queryDataModel: QueryDataModel) : PapersUiState

    data object Offline : PapersUiState
}