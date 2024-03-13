package com.brody.arxiv.designsystem.ui.chips

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.brody.arxiv.designsystem.dimens.Dimens
import com.brody.arxiv.designsystem.theme.OnSecondary
import com.brody.arxiv.designsystem.theme.Secondary

@Composable
fun ArxivChip(
    isSelected: Boolean,
    labelText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(labelText, overflow = TextOverflow.Ellipsis) },
        modifier = modifier
            .height(ChipDimens.chipHeight)
            .widthIn(
                ChipDimens.chipMinWidth, ChipDimens.chipMaxWidth
            ), // Spacing between chips,
        colors = FilterChipDefaults.filterChipColors(
            selectedLabelColor = OnSecondary, selectedContainerColor = Secondary
        )
    )
}

private object ChipDimens {

    val chipHeight = 32.dp
    val chipMinWidth = 30.dp
    val chipMaxWidth = 150.dp
}