package com.brody.arxiv.shared.search.presentation

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
import com.brody.arxiv.shared.settings.ai.domain.usecases.GetKeyUseCase
import com.brody.arxiv.shared.settings.ai.domain.usecases.SettingsAiUseCase
import com.brody.arxiv.shared.settings.ai.domain.usecases.TrySetNewKeyUseCase
import com.brody.arxiv.shared.settings.ai.domain.usecases.UpdateSelectedModelUseCase
import com.brody.arxiv.shared.settings.ai.models.domain.SettingsAiDataModel
import com.brody.arxiv.shared.settings.ai.models.presentation.LanguageUiModel
import com.brody.arxiv.shared.settings.ai.models.presentation.toDomainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
internal class SettingsAiViewModel @Inject constructor(
    settingsAiUseCase: SettingsAiUseCase,
    private val updateSelectedModelUseCase: UpdateSelectedModelUseCase,
    private val trySetNewKeyUseCase: TrySetNewKeyUseCase,
    private val getKeyUseCase: GetKeyUseCase,
    @Dispatcher(ArxivDispatchers.IO)
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    private lateinit var selectedLanguageModel: LanguageUiModel

    var dialogInputText: String? by mutableStateOf(null)
    private var lastKey: String? by mutableStateOf(null)


    val settingsAiState = settingsAiUseCase.invoke().map {
        SettingsAiUiState.Data(it)
    }.stateIn(
        ioViewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SettingsAiUiState.Connecting
    )

    private val _dialogUiState: MutableStateFlow<SettingsAiDialogUiState> =
        MutableStateFlow(SettingsAiDialogUiState.Idle())

    val dialogUiState: StateFlow<SettingsAiDialogUiState>
        get() = _dialogUiState

    private var keyHandlingJob: Job? = null
    private var keyInsertingJob: Job? = null

    fun openDialog(model: LanguageUiModel) {
        selectedLanguageModel = model
        _dialogUiState.update { SettingsAiDialogUiState.Opening(selectedLanguageModel) }

        keyHandlingJob = ioViewModelScope.launch {
            combine(
                getKeyUseCase(model.toDomainModel()),
                snapshotFlow { dialogInputText })
            { key, input ->
                SettingsAiDialogUiState.Editing(
                    selectedLanguageModel,
                    input ?: key.orEmpty(),
                    lastKey ?: key
                )
            }.collectLatest { latestState ->
                _dialogUiState.update { latestState }
            }
        }
    }

    fun closeDialog() {
        keyInsertingJob?.cancel()
        keyHandlingJob?.cancel()
        dialogInputText = null
        lastKey = null

        _dialogUiState.update {
            val isNewKey = it is SettingsAiDialogUiState.Inserting.Success
            SettingsAiDialogUiState.Idle(isNewKey)
        }
    }

    fun updateSelectedModel(model: LanguageUiModel) = ioViewModelScope.launch {
        updateSelectedModelUseCase(model.toDomainModel())
    }

    fun trySetNewKey(model: LanguageUiModel) {
        lastKey = dialogInputText
        keyInsertingJob = ioViewModelScope.launch {
            _dialogUiState.update { SettingsAiDialogUiState.Inserting.Progress(selectedLanguageModel) }

            val isSetSuccessfully =
                trySetNewKeyUseCase(model.toDomainModel(), dialogInputText.orEmpty())
            _dialogUiState.update {
                if (isSetSuccessfully)
                    SettingsAiDialogUiState.Inserting.Success(selectedLanguageModel) else
                    SettingsAiDialogUiState.Inserting.Failed(selectedLanguageModel)
            }
        }
    }

    private val ioViewModelScope: CoroutineScope
        get() = viewModelScope.plus(coroutineDispatcher)
}

@Immutable
internal sealed interface SettingsAiUiState {
    data object Connecting : SettingsAiUiState
    data class Data(
        val settingsAiDataModel: SettingsAiDataModel
    ) : SettingsAiUiState
}

@Immutable
internal open class SettingsAiDialogUiState(
    open val model: LanguageUiModel? = null
) {
    data class Idle(val isNewKey: Boolean = false) : SettingsAiDialogUiState()
    data class Opening(
        override val model: LanguageUiModel
    ) : SettingsAiDialogUiState(model)

    data class Editing(
        override val model: LanguageUiModel,
        val inputKey: String,
        val lastKey: String?
    ) : SettingsAiDialogUiState(model) {
        val isEnabled: Boolean
            get() = inputKey.isNotEmpty() && inputKey != lastKey
    }

    sealed class Inserting(override val model: LanguageUiModel) :
        SettingsAiDialogUiState(model) {
        class Progress(override val model: LanguageUiModel) : Inserting(model)
        class Success(override val model: LanguageUiModel) : Inserting(model)
        class Failed(override val model: LanguageUiModel) : Inserting(model)
    }
}