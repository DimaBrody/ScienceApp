package com.brody.arxiv.designsystem.ui.list

import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ArxivListItem(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    isSecondary: Boolean = false,
    containerColor: @Composable () -> Color = {
        if (!isSecondary) MaterialTheme.colorScheme.background
        else MaterialTheme.colorScheme.surface
    },
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    ListItem(
        headlineContent = headlineContent,
        modifier = modifier,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = ListItemDefaults.colors(
            containerColor = containerColor()
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    )
}