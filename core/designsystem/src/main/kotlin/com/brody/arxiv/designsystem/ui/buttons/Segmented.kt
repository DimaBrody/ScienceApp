package com.brody.arxiv.designsystem.ui.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import com.brody.arxiv.designsystem.ui.icons.ArxivIcons

@Composable
fun SingleChoiceSegmentedButtonRowScope.ArxivSegmentedButton(
    index: Int,
    count: Int,
    selected: Boolean,
    isHighlight: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = { },
    text: String? = null,
    label: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
) {
    val arxivSegmentedColors = ArxivSegmentedButtonDefaults.colors(false)

    SegmentedButton(shape = SegmentedButtonDefaults.itemShape(index = index, count = count),
        icon = icon,
        onClick = onClick,
        selected = selected,
        enabled = enabled,
        modifier = modifier,
        colors = arxivSegmentedColors,
        label = label ?: {
            Text(
                text = text.orEmpty(),
                color = ArxivSegmentedButtonDefaults
                    .getHighlightInactiveContentColor(selected),
                textDecoration = if(isHighlight) TextDecoration.Underline else TextDecoration.None
            )
        })
}

object ArxivSegmentedButtonDefaults {

    private val primaryContainerColor
        @Composable get() = MaterialTheme.colorScheme.primaryContainer

    private val onPrimaryContainerColor
        @Composable get() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun colors(isHighlight: Boolean) = SegmentedButtonDefaults.colors(
        activeContainerColor = primaryContainerColor,
        activeContentColor = onPrimaryContainerColor,
        inactiveContainerColor = Color.Transparent,
        inactiveContentColor = getHighlightInactiveContentColor(isHighlight),
        activeBorderColor = MaterialTheme.colorScheme.outline,
        inactiveBorderColor = getHighlightInactiveBorderColor(isHighlight)
    )

    @Composable
    fun getHighlightInactiveContentColor(isHighlight: Boolean) =
        if (isHighlight) onPrimaryContainerColor
        else MaterialTheme.colorScheme.onSurface

    @Composable
    private fun getHighlightInactiveBorderColor(isHighlight: Boolean) =
        if (isHighlight) onPrimaryContainerColor
        else MaterialTheme.colorScheme.outline
}