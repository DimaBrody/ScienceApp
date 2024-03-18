package com.brody.arxiv.shared.filters.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brody.arxiv.designsystem.theme.ArxivTheme
import com.brody.arxiv.designsystem.theme.FilterTitleTextTheme
import com.brody.arxiv.designsystem.ui.appbar.ArxivTopAppBar
import com.brody.arxiv.designsystem.ui.dialogs.AnimatedFullScreenDialog
import com.brody.arxiv.designsystem.ui.icons.ArxivIcons
import com.brody.arxiv.shared.papers.models.presentation.DisplayableEnum
import com.brody.arxiv.shared.papers.models.presentation.SortBy
import com.brody.arxiv.shared.papers.models.presentation.SortOrder
import com.brody.arxiv.shared.settings.models.domain.QueryDataModel
import com.brody.arxiv.shared.subjects.models.presentation.SubjectsRequest
import com.brody.arxiv.shared.subjects.presentation.ui.chips.SubjectsChipsContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


@Composable
fun FiltersDialog(
    queryDataModel: QueryDataModel,
    isShowDialog: MutableState<Boolean>,
    onQueryDataChanged: (QueryDataModel) -> Unit
) {
    AnimatedFullScreenDialog(
        isShowDialog.value,
        onDismissRequest = { isShowDialog.value = false },
    ) {
        FiltersContent(
            queryDataModel = queryDataModel,
            isShowDialog = isShowDialog,
            onQueryDataChanged = onQueryDataChanged
        )
    }
}

@Composable
private fun FiltersContent(
    queryDataModel: QueryDataModel,
    isShowDialog: MutableState<Boolean>,
    onQueryDataChanged: (QueryDataModel) -> Unit,
    filtersViewModel: FiltersViewModel = hiltViewModel()
) {
//    filtersViewModel.restore()
    var isFirstLaunch by rememberSaveable { mutableStateOf(true) }
    if (isFirstLaunch) {
        filtersViewModel.restore()
        isFirstLaunch = false
    }

    filtersViewModel.setInitial(queryDataModel)

    FilterContentInternal(
        isShowDialog = isShowDialog,
        uiStateFlow = filtersViewModel.uiState,
        updateInnerQueryData = filtersViewModel::updateQuery,
        onQueryDataChanged = onQueryDataChanged
    )
}

@Composable
private fun FilterContentInternal(
    isShowDialog: MutableState<Boolean>,
    uiStateFlow: StateFlow<FiltersUiState>,
    updateInnerQueryData: (QueryDataModel.() -> QueryDataModel) -> Unit,
    onQueryDataChanged: (QueryDataModel) -> Unit,
) {
    val uiState by uiStateFlow.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            ArxivTopAppBar(titleRes = R.string.shared_filters_title,
                actionIcons = if (uiState is FiltersUiState.NotUpdate) listOf(ArxivIcons.Close)
                else listOf(ArxivIcons.Check),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                onActionClicks = listOf {
                    isShowDialog.value = false
                    (uiState as? FiltersUiState.Update)?.let {
                        onQueryDataChanged(it.queryDataModel)
                    }
                })
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier.fillMaxHeight()
    ) { padding ->
        Column(
            Modifier.padding(padding)
        ) {
            uiState.queryDataModel?.let { queryState ->
//                val sortByTitle = stringResource(R.string.shared_filters_sort_by)
//                val sortOrderTitle = stringResource(R.string.shared_filters_sort_order)

                RadioListComponent<SortBy>(titleName = "Sort by",
                    currentSelectedItem = { queryState.sortByOrdinal }) {
                    updateInnerQueryData {
                        copy(sortByOrdinal = it)
                    }
                }

                RadioListComponent<SortOrder>(titleName = "Sort order",
                    currentSelectedItem = { queryState.sortOrderOrdinal }) {
                    updateInnerQueryData {
                        copy(sortOrderOrdinal = it)
                    }
                }

                Spacer(Modifier.height(24.dp))

                SubjectsChipsContent(
                    subjectsRequest = SubjectsRequest.Filters(queryState.excludedIds),
                    onSubjectChipsUpdated = { subjectChips ->
                        updateInnerQueryData {
                            copy(excludedIds = subjectChips.map {
                                it.link.createLinkFromBits()
                            }.toSet())
                        }
                    })
            }


        }
    }
}

@Composable
private inline fun <reified T> RadioListComponent(
    titleName: String,
    currentSelectedItem: () -> Int,
    crossinline updateInnerQueryData: (Int) -> Unit,
) where T : Enum<T>, T : DisplayableEnum {

    val sortByEntries = enumValues<T>()
    val selectedSortBy = sortByEntries[currentSelectedItem()]

    Spacer(Modifier.height(24.dp))
    Text(
        text = titleName, modifier = Modifier.padding(start = 16.dp), style = FilterTitleTextTheme
    )
    Spacer(Modifier.height(8.dp))
    sortByEntries.forEach { entry ->
        Row(
            Modifier
                .height(56.dp)
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RadioButton(selected = (entry == selectedSortBy), onClick = {
                updateInnerQueryData(entry.ordinal)
            })
            Text(
                text = entry.displayName, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(widthDp = 360, heightDp = 600, backgroundColor = 0xFFFFF4F3)
@Composable
private fun PreviewFiltersDialog() {
    ArxivTheme {
        val queryModel = QueryDataModel(0, 0, emptySet())
        FilterContentInternal(uiStateFlow = MutableStateFlow(FiltersUiState.Update(queryModel)),
            isShowDialog = mutableStateOf(true),
            onQueryDataChanged = {

            },
            updateInnerQueryData = {

            })
    }
}