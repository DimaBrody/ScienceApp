package com.brody.arxiv.shared.search.models.presentation

import kotlinx.coroutines.flow.StateFlow

sealed interface SearchState {
    data object Inactive : SearchState
    data class Query(val value: String) : SearchState
}