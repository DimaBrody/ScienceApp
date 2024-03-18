package com.brody.arxiv.features.details.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brody.arxiv.core.threading.ArxivDispatchers
import com.brody.arxiv.core.threading.Dispatcher
import com.brody.arxiv.features.details.presentation.models.DetailsUiModel
import com.brody.arxiv.features.details.presentation.models.toSaveableModel
import com.brody.arxiv.shared.saved.domain.usecases.SavedPapersIdsUseCase
import com.brody.arxiv.shared.saved.domain.usecases.ToggleSaveItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val toggleSaveItemUseCase: ToggleSaveItemUseCase,
    @Dispatcher(ArxivDispatchers.IO) private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val currentItem: MutableStateFlow<DetailsUiModel?> = MutableStateFlow(null)

    val uiState: StateFlow<DetailsUiState> = currentItem.map {
        if (it == null) DetailsUiState.Waiting
        else DetailsUiState.Connected(it)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        DetailsUiState.Waiting
    )

    fun saveItem(id: String, item: DetailsUiModel) {
        viewModelScope.launch(coroutineDispatcher) {
            toggleSaveItemUseCase.invoke(
                id, if (item.isSaved) null
                else item.toSaveableModel()
            )
            currentItem.update {
                it?.copy(isSaved = !item.isSaved)
            }
        }
    }

    fun connectDetailsItem(model: DetailsUiModel) {
        currentItem.update { model }
    }

}

sealed interface DetailsUiState {
    data object Waiting : DetailsUiState
    data class Connected(
        val model: DetailsUiModel
    ) : DetailsUiState
}