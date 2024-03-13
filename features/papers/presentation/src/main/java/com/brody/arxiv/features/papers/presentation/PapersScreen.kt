package com.brody.arxiv.features.papers.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brody.arxiv.core.common.actions.MainToolbarAction
import com.brody.arxiv.shared.filters.presentation.FiltersDialog
import com.brody.arxiv.shared.papers.models.presentation.FetchPapers
import com.brody.arxiv.shared.papers.presentation.ui.PapersList
import com.brody.arxiv.shared.settings.models.domain.QueryDataModel
import com.brody.arxiv.shared.settings.models.domain.toFetchPapers
import kotlinx.coroutines.flow.SharedFlow

@Composable
internal fun PapersScreen(
    toolbarActionFlow: SharedFlow<MainToolbarAction>,
    viewModel: PapersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val queryDataModel = (uiState as? PapersUiState.Result)?.queryDataModel

    queryDataModel?.let { PapersList(it.toFetchPapers()) }

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
