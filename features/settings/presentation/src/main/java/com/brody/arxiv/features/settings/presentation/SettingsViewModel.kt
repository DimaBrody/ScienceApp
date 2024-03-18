package com.brody.arxiv.features.settings.presentation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brody.arxiv.core.threading.ArxivDispatchers
import com.brody.arxiv.core.threading.Dispatcher
import com.brody.arxiv.features.settings.presentation.models.SettingsGeneralUiModel
import com.brody.arxiv.features.settings.presentation.models.toPresentationModel
import com.brody.arxiv.features.settings.presentation.models.toSaveModel
import com.brody.arxiv.shared.settings.domain.usecases.GetSettingsUseCase
import com.brody.arxiv.shared.settings.domain.usecases.UpdateSettingsUseCase
import com.brody.arxiv.shared.settings.models.domain.SettingsDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    @Dispatcher(ArxivDispatchers.IO) private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {
    val uiState = getSettingsUseCase().map {
        SettingsUiState.Result(it.toPresentationModel())
    }.stateIn(
        viewModelScope.plus(coroutineDispatcher),
        SharingStarted.WhileSubscribed(5_000),
        SettingsUiState.Loading
    )

    fun onUpdateValues(settingsUiModel: SettingsGeneralUiModel) {
        viewModelScope.launch(coroutineDispatcher) {
            updateSettingsUseCase(settingsUiModel.toSaveModel())
        }
    }

}

internal sealed interface SettingsUiState {
    data object Loading

    @Immutable
    data class Result(
        val settingsUiModel: SettingsGeneralUiModel
    )
}