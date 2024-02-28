package com.brody.arxiv.shared.subjects.presentation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brody.arxiv.designsystem.theme.OnSecondary
import com.brody.arxiv.designsystem.theme.Secondary
import com.brody.arxiv.shared.subjects.models.presentation.SubjectChipData

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipsFlowRow(
    modifier: Modifier = Modifier,
    subjectsViewModel: SubjectsViewModel = hiltViewModel(),
    onSubjectChipsUpdated: (List<SubjectChipData>) -> Unit
) {
    val uiState by subjectsViewModel.uiState.collectAsStateWithLifecycle()

    if (uiState is SubjectsUiState.Fetched) {
        val scroll = rememberScrollState(0)
        val chips = (uiState as SubjectsUiState.Fetched).chips
        onSubjectChipsUpdated(chips.filter { it.second }.map { it.first })

        FlowRow(
            modifier
                .fillMaxWidth(1f)
                .wrapContentHeight(align = Alignment.Top)
                .verticalScroll(scroll)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            chips.forEach { chip ->
                val isSelected = chip.second
                FilterChip(
                    selected = isSelected,
                    onClick = { subjectsViewModel.selectionChanged(chip.first, !isSelected) },
                    label = { Text(chip.first.name, overflow = TextOverflow.Ellipsis) },
                    modifier = Modifier
                        .padding(
                            horizontal = 2.dp,
                            vertical = 4.dp
                        )
                        .height(32.dp)
                        .widthIn(30.dp, 150.dp), // Spacing between chips,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedLabelColor = OnSecondary,
                        selectedContainerColor = Secondary
                    )
                )
            }
        }
    }
}
