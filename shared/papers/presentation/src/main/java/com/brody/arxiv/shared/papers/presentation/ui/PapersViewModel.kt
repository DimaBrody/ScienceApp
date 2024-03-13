package com.brody.arxiv.shared.papers.presentation.ui

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.brody.arxiv.core.threading.ArxivDispatchers.IO
import com.brody.arxiv.core.threading.Dispatcher
import com.brody.arxiv.shared.papers.domain.usecases.QueryPapersUseCase
import com.brody.arxiv.shared.papers.domain.usecases.QueryWithSubjectsPapersUseCase
import com.brody.arxiv.shared.papers.models.domain.FetchPapersDomain
import com.brody.arxiv.shared.papers.models.domain.PaperDomainModel
import com.brody.arxiv.shared.papers.models.presentation.FetchPapers
import com.brody.arxiv.shared.papers.models.presentation.PaperUiModel
import com.brody.arxiv.shared.papers.models.presentation.toDomainRequest
import com.brody.arxiv.shared.papers.models.presentation.toPresentationModel
import com.brody.arxiv.shared.saved.domain.usecases.SavedPapersIdsUseCase
import com.brody.arxiv.shared.saved.domain.usecases.SavedPapersUseCase
import com.brody.arxiv.shared.saved.domain.usecases.ToggleSaveItem
import com.brody.arxiv.shared.saved.models.domain.toSaveModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PapersViewModel @Inject constructor(
    private val queryWithSubjects: QueryWithSubjectsPapersUseCase,
    private val queryDefault: QueryPapersUseCase,
    private val querySaved: SavedPapersUseCase,
    private val savedPapersIds: SavedPapersIdsUseCase,
    private val toggleSaveItem: ToggleSaveItem,
    @Dispatcher(IO) private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var _uiState = MutableStateFlow<PapersUiState>(PapersUiState.Loading)
    private var isFirstLaunch: Boolean = true

    val uiState: () -> StateFlow<PapersUiState>
        get() = { _uiState }

    fun getPapers(fetchPapers: FetchPapers) {
        viewModelScope.launch(coroutineDispatcher) {
            setLoading()

            if (isFirstLaunch) {
                delay(100)
                isFirstLaunch = false
            }

            _uiState.update {
                when (fetchPapers) {
                    is FetchPapers.Remote.Offline, is FetchPapers.Remote.Query.Default -> PapersUiState.Connected.from(
                        queryDefault(fetchPapers.toDomainRequest() as FetchPapersDomain.Remote).cacheWithSaved()
                    )

                    is FetchPapers.Remote.Query.Subjects -> PapersUiState.Connected.from(
                        queryWithSubjects(fetchPapers.toDomainRequest() as FetchPapersDomain.Remote).cacheWithSaved()
                    )

                    is FetchPapers.Saved -> PapersUiState.Connected.from(
                        querySaved().cacheWithSaved()
                    )

                    else -> PapersUiState.Loading
                }
            }
        }
    }

    private fun setLoading() {
        _uiState.update { PapersUiState.Loading }
    }

    fun saveItem(id: String, item: PaperUiModel?) {
        viewModelScope.launch(coroutineDispatcher) {
            toggleSaveItem.invoke(id, item?.toSaveModel())
        }
    }

    private suspend fun Flow<PagingData<PaperDomainModel>>.cacheWithSaved(): Flow<PagingData<PaperDomainModel>> =
        cachedIn(viewModelScope).combine(savedPapersIds.invoke()) { papers, savedIds ->
            papers.map { paper -> paper.also { it.isSaved = it.id in savedIds } }
        }
}

@Immutable
sealed interface PapersUiState {
    data object Loading : PapersUiState
    class Connected(
        val pagingData: Flow<PagingData<PaperUiModel>>
    ) : PapersUiState {
        companion object {
            fun from(domain: Flow<PagingData<PaperDomainModel>>) =
                Connected(domain.map { it.map { paper -> paper.toPresentationModel() } })
        }
    }
}