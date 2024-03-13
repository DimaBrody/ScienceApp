package com.brody.arxiv.shared.subjects.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brody.arxiv.designsystem.theme.FilterTitleText
import com.brody.arxiv.designsystem.ui.chips.ArxivChip
import com.brody.arxiv.shared.subjects.models.presentation.SubjectChipData
import com.brody.arxiv.shared.subjects.models.presentation.SubjectsRequest
import com.brody.arxiv.shared.subjects.presentation.dimens.SubjectsDimens

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubjectsChipsContent(
    subjectsRequest: SubjectsRequest,
    modifier: Modifier = Modifier,
    subjectsViewModel: SubjectsViewModel = hiltViewModel(),
    onSubjectChipsUpdated: (List<SubjectChipData>) -> Unit,
    content: @Composable (() -> Unit)? = null
) {
    subjectsViewModel.requestSubjects(subjectsRequest)

    val uiState by subjectsViewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is SubjectsUiState.FetchedOnboarding -> {
            val scroll = rememberScrollState(0)
            val chips = (uiState as SubjectsUiState.FetchedOnboarding).chips
            onSubjectChipsUpdated(chips.filter { it.second }.map { it.first })

            FlowRow(
                modifier
                    .fillMaxWidth(1f)
                    .wrapContentHeight(align = Alignment.Top)
                    .verticalScroll(scroll)
                    .padding(horizontal = SubjectsDimens.flowRowPadding),
                horizontalArrangement = Arrangement.spacedBy(
                    SubjectsDimens.horizontalSpacing, Alignment.CenterHorizontally
                ),
                verticalArrangement = Arrangement.spacedBy(SubjectsDimens.verticalSpacing),
            ) {
                chips.forEach { chip ->
                    val isSelected = chip.second
                    ArxivChip(
                        isSelected = isSelected,
                        onClick = { subjectsViewModel.selectionChanged(chip.first, !isSelected) },
                        labelText = chip.first.name,
                        modifier = Modifier.padding(
                            horizontal = SubjectsDimens.chipHorizontalPadding,
                            vertical = SubjectsDimens.chipVerticalPadding
                        )
                    )
                }
            }

            content?.invoke()
        }

        is SubjectsUiState.FetchedFilters -> {
            val excludedNodes = (uiState as SubjectsUiState.FetchedFilters).excludedNodes
            onSubjectChipsUpdated(excludedNodes)

            val chips = (uiState as SubjectsUiState.FetchedFilters).chips


            chips.entries.forEach {
                Column {
                    Text(
                        text = it.key,
                        style = FilterTitleText,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Spacer(Modifier.height(20.dp))

                    val state = rememberLazyStaggeredGridState()
                    LazyHorizontalStaggeredGrid(
                        modifier = Modifier.height(2 * 30.dp + 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        rows = StaggeredGridCells.Adaptive(30.dp),
                        horizontalItemSpacing = 8.dp,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        state = state,
                    ) {
                        items(it.value, key = { it.first.link }) {
                            val isSelected = it.second
                            ArxivChip(
                                isSelected = isSelected,
                                labelText = it.first.name,
                                onClick = {
                                    subjectsViewModel.selectionChanged(it.first, !isSelected)
                                })
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            content?.invoke()
        }

        else -> {}
    }
}
