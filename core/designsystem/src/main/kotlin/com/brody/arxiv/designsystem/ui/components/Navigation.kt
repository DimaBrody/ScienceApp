package com.brody.arxiv.designsystem.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        tonalElevation = 0.dp,
        contentColor = ArxivNavigationDefaults.navigationContentColor(),
        containerColor = ArxivNavigationDefaults.navigationSurface(),
        content = content,
    )
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