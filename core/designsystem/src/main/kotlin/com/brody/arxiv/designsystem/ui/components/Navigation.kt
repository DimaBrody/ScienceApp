package com.brody.arxiv.designsystem.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.ArxivNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: @Composable () -> Unit = icon,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = ArxivNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = ArxivNavigationDefaults.navigationContentColor(),
            selectedTextColor = ArxivNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = ArxivNavigationDefaults.navigationContentColor(),
            indicatorColor = ArxivNavigationDefaults.navigationIndicatorColor()
        ),
    )
}

@Composable
fun ArxivNavigationBar(
    modifier: Modifier = Modifier,
    isCustom: Boolean = false,
    content: @Composable RowScope.() -> Unit,
) {
    val contentColor = ArxivNavigationDefaults.navigationContentColor()
    val containerColor = ArxivNavigationDefaults.navigationSurface()

    if (isCustom) {
        CustomNavigationBar(
            modifier = modifier,
            tonalElevation = 0.dp,
            contentColor = contentColor,
            containerColor = containerColor,
            content = content,
        )
    } else {
        NavigationBar(
            modifier = modifier,
            tonalElevation = 0.dp,
            contentColor = contentColor,
            containerColor = containerColor,
            content = content,
        )
    }

}

@Composable
fun CustomNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = NavigationBarDefaults.containerColor,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
    tonalElevation: Dp = NavigationBarDefaults.Elevation,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(windowInsets)
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

object ArxivNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer

    @Composable
    fun navigationSurface() = MaterialTheme.colorScheme.surface
}

@Preview
@Composable
fun BottomBarPreview() {

}