package com.brody.arxiv.features.summary.presentation.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brody.arxiv.core.threading.ArxivDispatchers
import com.brody.arxiv.core.threading.Dispatcher
import com.brody.arxiv.features.summary.presentation.models.SummaryUiModel
import com.brody.arxiv.features.summary.presentation.models.toSystemMessage
import com.brody.arxiv.features.summary.presentation.models.toPresentationModel
import com.brody.arxiv.features.summary.presentation.ui.settings.SummarySettingsAiUiState
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import com.brody.arxiv.shared.settings.ai.domain.usecases.SettingsAiUseCase
import com.brody.arxiv.shared.summary.domain.usecases.GetSummarySettingsUseCase
import com.brody.arxiv.shared.summary.domain.usecases.GetSummaryUseCase
import com.brody.arxiv.shared.summary.domain.usecases.UpdateSummaryTypeUseCase
import com.brody.arxiv.shared.summary.models.domain.SavedSummary
import com.brody.arxiv.shared.summary.models.domain.SettingsSummaryDataModel
import com.brody.arxiv.shared.summary.models.domain.SummaryType
import com.brody.arxiv.work.summary.manager.SummaryWorkerManager
import com.brody.arxiv.work.summary.models.SummaryWorkerPromptsInfo
import com.brody.arxiv.work.summary.models.SummaryWorkerState
import com.brody.arxiv.work.summary.models.toProcessingText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.annotation.concurrent.Immutable
import javax.inject.Inject


@HiltViewModel
internal class SummaryViewModel @Inject constructor(
    @Dispatcher(ArxivDispatchers.IO) private val coroutineDispatcher: CoroutineDispatcher,
    private val summaryWorkerManager: SummaryWorkerManager,
    private val getSummaryUseCase: GetSummaryUseCase,
    private val updateSummaryTypeUseCase: UpdateSummaryTypeUseCase,
    summarySettings: GetSummarySettingsUseCase,
    settingsAiUseCase: SettingsAiUseCase,
) : ViewModel() {

    private lateinit var saveableModel: SaveablePaperDataModel

    private lateinit var savedSummaries: Flow<SavedSummary?>
    private var summaryFlow: StateFlow<SummaryUiState>? = null

    private val _existingSummaries: MutableStateFlow<ImmutableList<SummaryType>> =
        MutableStateFlow(persistentListOf())

    val existingSummaries: StateFlow<ImmutableList<SummaryType>>
        get() = _existingSummaries


    val settingsFlow: StateFlow<SummarySettingsUiState> =
        summarySettings().map { SummarySettingsUiState.Fetched(it) }.stateIn(
            ioViewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SummarySettingsUiState.Connecting
        )

    val settingsAiState: StateFlow<SummarySettingsAiUiState> = settingsAiUseCase().map {
        val selectedModel = it.selectedModel
        val modelConfig = it.modelsConfig[selectedModel]
        val hasKey = modelConfig?.hasKey ?: false

        if (hasKey) {
            SummarySettingsAiUiState.Fetched(selectedModel, modelConfig!!)
        } else SummarySettingsAiUiState.FetchedNothing
    }.stateIn(
        ioViewModelScope, SharingStarted.WhileSubscribed(5_000), SummarySettingsAiUiState.Connecting
    )

    private val _currentItem: MutableStateFlow<SummaryUiModel?> = MutableStateFlow(null)
    val currentItem: StateFlow<SummaryUiModel?>
        get() = _currentItem


    fun connectSummaryFlow(model: SaveablePaperDataModel): StateFlow<SummaryUiState> {
        if (summaryFlow == null) {
            connectItems(model)
        }
        return summaryFlow!!
    }

    private fun connectItems(model: SaveablePaperDataModel) {
        this.saveableModel = model
        _currentItem.update { model.toPresentationModel() }

        initSavedSummaries()
        initSummaryFlow()
    }

    private fun initSavedSummaries() {
        savedSummaries = getSummaryUseCase(saveableModel.id)
    }

    private val savedSummaryFlow: Flow<String?>
        get() = combine(settingsFlow, savedSummaries) { settings, savedSummaries ->
            if (settings is SummarySettingsUiState.Fetched) {
                val currentType = settings.summarySettings.summaryType
                val summaries = savedSummaries?.summaries

                summaries?.let { _existingSummaries.tryEmit(it.keys.toImmutableList()) }
                summaries?.get(currentType)
            } else null
        }

    private val workerFlow: Flow<SummaryWorkerState>
        get() = summaryWorkerManager.workerFlow(saveableModel.id)

    private val workerIsRunningFlow: Flow<Boolean>
        get() = summaryWorkerManager.isRunning(saveableModel.id)


    private fun initSummaryFlow() {
        summaryFlow = combine(
            savedSummaryFlow, workerFlow, workerIsRunningFlow
        ) { savedSummary, workerState, isWorkerRunning ->

            if (savedSummary != null && !isWorkerRunning) {
                SummaryUiState.Fetched(savedSummary)
            } else workerState.toUiState()
        }.filter { it !is SummaryUiState.Saving }.stateIn(
            ioViewModelScope, SharingStarted.WhileSubscribed(5_000), SummaryUiState.Idle()
        )
    }

    fun updateSummaryType(summaryType: SummaryType) = ioViewModelScope.launch {
        updateSummaryTypeUseCase(summaryType)
    }

    fun startSummaryWork(chunkPrompt: String? = null, finalPrompt: String? = null) {
        val selectedSummaryType = (settingsFlow.value as?
                SummarySettingsUiState.Fetched)?.summarySettings?.summaryType ?: SummaryType.FAN

        val summaryPromptsInfo = SummaryWorkerPromptsInfo(
            summaryType = selectedSummaryType,
            chunkPrompt = chunkPrompt,
            finalPrompt = finalPrompt,
            systemMessage = selectedSummaryType.toSystemMessage(),
        )

        summaryWorkerManager.startSummaryWork(
            saveableModel, summaryPromptsInfo
        )
    }

    fun cancelWork() {
        summaryWorkerManager.cancelWork(saveableModel.id)
    }

    private val ioViewModelScope: CoroutineScope
        get() = viewModelScope.plus(coroutineDispatcher)
}

@Immutable
internal sealed interface SummarySettingsUiState {
    data object Connecting : SummarySettingsUiState
    data class Fetched(val summarySettings: SettingsSummaryDataModel) : SummarySettingsUiState
}

@Immutable
internal sealed class SummaryUiState(
    open val text: String
) {
    data class PdfExtracting(override val text: String) : SummaryUiState(text)
    data class TextSplitting(override val text: String) : SummaryUiState(text)
    data class Saving(override val text: String) : SummaryUiState(text)
    data class Reduce(override val text: String) : SummaryUiState(text)
    data class Summarizing(override val text: String) : SummaryUiState(text)
    data class Printing(override val text: String) : SummaryUiState(text)

    // Check is running
    data class Fetched(override val text: String) : SummaryUiState(text)
    data class Failed(override val text: String) : SummaryUiState(text)

    data class Idle(
        override val text: String = SummaryWorkerState.Connecting.toProcessingText(false)
    ) : SummaryUiState(text)
}

@Immutable
internal sealed interface SummaryScrollUiState {
    data object ButtonClickScroll : SummaryScrollUiState
    data object StickScroll : SummaryScrollUiState
    data object Idle : SummaryScrollUiState
}

private fun SummaryWorkerState.toUiState(): SummaryUiState {
    val text = toProcessingText(false)
    return when (this) {
        is SummaryWorkerState.Connecting -> SummaryUiState.Idle(text)
        is SummaryWorkerState.PdfExtracting -> SummaryUiState.PdfExtracting(text)
        is SummaryWorkerState.TextSplitting -> SummaryUiState.TextSplitting(text)
        is SummaryWorkerState.Saving -> SummaryUiState.Saving(text)
        is SummaryWorkerState.Summarizing -> SummaryUiState.Summarizing(text)
        is SummaryWorkerState.Output -> SummaryUiState.Printing(text)
        is SummaryWorkerState.Reduce -> SummaryUiState.Reduce(text)
        is SummaryWorkerState.Finished -> SummaryUiState.Fetched(text)
        is SummaryWorkerState.Failure -> SummaryUiState.Failed(text)
    }
}

internal fun SummaryUiState.isIdle(exceptFetched: Boolean = false) =
    this is SummaryUiState.Idle || this is SummaryUiState.Failed || (!exceptFetched && this is SummaryUiState.Fetched)