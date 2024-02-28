package com.brody.arxiv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brody.arxiv.shared.subjects.domain.repository.SubjectsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    subjectsRepository: SubjectsRepository
) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> = subjectsRepository.userSubjects.map {
        MainActivityUiState.Success(!it.items.isNullOrEmpty())
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainActivityUiState.Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(
        val isSubjectsPresent: Boolean,
        val isDarkTheme: Boolean = false
    ) : MainActivityUiState
}
