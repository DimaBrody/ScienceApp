package com.brody.arxiv.features.papers.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brody.arxiv.core.common.actions.MainToolbarAction
import com.brody.arxiv.core.common.typealiases.ScrollListener
import com.brody.arxiv.shared.filters.presentation.FiltersDialog
import com.brody.arxiv.shared.papers.models.presentation.FetchPapers
import com.brody.arxiv.shared.papers.presentation.ui.PapersList
import com.brody.arxiv.shared.saved.models.domain.OnPaperClicked
import com.brody.arxiv.shared.settings.general.models.domain.QueryDataModel
import com.brody.arxiv.shared.settings.general.models.domain.toFetchPapers
import kotlinx.coroutines.flow.SharedFlow

@Composable
internal fun PapersScreen(
    toolbarActionFlow: SharedFlow<MainToolbarAction>,
    scrollListener: ScrollListener,
    onPaperClicked: OnPaperClicked,
    viewModel: PapersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val queryDataModel = (uiState as? PapersUiState.Query)?.queryDataModel


    when (uiState) {
        is PapersUiState.Query -> (uiState as? PapersUiState.Query)?.let {
            PapersList(
                fetchPapers = it.queryDataModel.toFetchPapers(),
                onPaperClicked = onPaperClicked,
                onOffline = viewModel::updateOfflineState,
                onScrollListener = scrollListener,
            )
        }

        is PapersUiState.Offline -> {
            PapersList(
                fetchPapers = FetchPapers.Remote.Offline,
                onPaperClicked = onPaperClicked,
                onOffline = viewModel::updateOfflineState,
                onScrollListener = scrollListener
            )
        }

        else -> {}
    }

    FiltersComponent(
        queryDataModel = queryDataModel,
        toolbarActionFlow = toolbarActionFlow,
        onQueryDataChanged = viewModel::updateQuery
    )
}

@Composable
private fun FiltersComponent(
    queryDataModel: QueryDataModel?,
    toolbarActionFlow: SharedFlow<MainToolbarAction>,
    onQueryDataChanged: (QueryDataModel) -> Unit
) {
    val isShowDialog = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        toolbarActionFlow.collect {
            if (it == MainToolbarAction.FILTERS) isShowDialog.value = true
        }
    }

    queryDataModel?.let {
        FiltersDialog(
            queryDataModel = it,
            isShowDialog = isShowDialog,
            onQueryDataChanged = onQueryDataChanged
        )
    }

}
