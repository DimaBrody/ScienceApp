package com.brody.arxiv.features.summary.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brody.arxiv.core.threading.ArxivDispatchers
import com.brody.arxiv.core.threading.Dispatcher
import com.brody.arxiv.features.summary.presentation.models.SummaryUiLanguage
import com.brody.arxiv.features.summary.presentation.models.toDomainModel
import com.brody.arxiv.features.summary.presentation.models.toUiModel
import com.brody.arxiv.features.summary.presentation.ui.screen.SummarySettingsUiState
import com.brody.arxiv.shared.settings.ai.domain.usecases.SettingsAiUseCase
import com.brody.arxiv.shared.settings.ai.domain.usecases.UpdateConfigUseCase
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModel
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModelConfig
import com.brody.arxiv.shared.summary.domain.usecases.GetSummarySettingsUseCase
import com.brody.arxiv.shared.summary.domain.usecases.UpdateSummaryLanguageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
internal class SummarySettingsViewModel @Inject constructor(
    @Dispatcher(ArxivDispatchers.IO) private val coroutineDispatcher: CoroutineDispatcher,
    settingsAiUseCase: SettingsAiUseCase,
    summarySettingsUseCase: GetSummarySettingsUseCase,
    private val updateSummaryLanguageUseCase: UpdateSummaryLanguageUseCase,
    private val settingsUpdateConfigUseCase: UpdateConfigUseCase
) : ViewModel() {

    val summarySettings = summarySettingsUseCase.invoke().map {
        SummarySettingsLanguage.Fetched(it.summaryLanguage.toUiModel())
    }.stateIn(
        ioCoroutineScope,
        SharingStarted.WhileSubscribed(5_000),
        SummarySettingsLanguage.Connecting
    )

    val settingsUiState: StateFlow<SummarySettingsAiUiState> = settingsAiUseCase().map {
        val selectedModel = it.selectedModel
        val modelConfig = it.modelsConfig[selectedModel]
        val hasKey = modelConfig?.hasKey ?: false

        if (hasKey) {
            SummarySettingsAiUiState.Fetched(selectedModel, modelConfig!!)
        } else SummarySettingsAiUiState.FetchedNothing
    }.stateIn(
        ioCoroutineScope, SharingStarted.WhileSubscribed(5_000), SummarySettingsAiUiState.Connecting
    )

    fun toggleStream() = ioCoroutineScope.launch {
        fetchedValues?.let {
            val (languageModel, languageModelConfig) = it
            val newConfig = languageModelConfig.copy(isStream = !languageModelConfig.isStream)
            settingsUpdateConfigUseCase(languageModel, newConfig)
        }
    }

    fun updateLanguage(language: SummaryUiLanguage) = ioCoroutineScope.launch {
        updateSummaryLanguageUseCase(language.toDomainModel())
    }

    private var debounceJob: Job? = null

    fun setTemperature(
        value: Float
    ) {
        debounceJob?.cancel()
        debounceJob = ioCoroutineScope.launch {
            fetchedValues?.let {
                delay(200)
                val (languageModel, languageModelConfig) = it
                val newConfig = languageModelConfig.copy(temperature = value)
                settingsUpdateConfigUseCase(languageModel, newConfig)
            }

        }
    }

    private val fetchedValues: SummarySettingsAiUiState.Fetched?
        get() = settingsUiState.value as? SummarySettingsAiUiState.Fetched

    private val ioCoroutineScope: CoroutineScope
        get() = viewModelScope.plus(coroutineDispatcher)
}

internal sealed interface SummarySettingsLanguage {
    data object Connecting : SummarySettingsLanguage
    data class Fetched(
        val language: SummaryUiLanguage
    ) : SummarySettingsLanguage
}

internal sealed interface SummarySettingsAiUiState {
    data object Connecting : SummarySettingsAiUiState
    data object FetchedNothing : SummarySettingsAiUiState
    data class Fetched(
        val languageModel: LanguageModel,
        val languageModelConfig: LanguageModelConfig
    ) : SummarySettingsAiUiState
}