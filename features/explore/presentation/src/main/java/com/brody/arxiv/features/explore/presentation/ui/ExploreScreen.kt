package com.brody.arxiv.features.explore.presentation.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelStoreOwner
import com.brody.arxiv.shared.search.models.presentation.SearchState
import com.brody.arxiv.shared.subjects.presentation.ui.list.SubjectsList
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ExploreScreen(
    searchFlow: StateFlow<SearchState>
) {
    SubjectsList(
        searchFlow = searchFlow,
        isAlwaysActive = true
    )
}