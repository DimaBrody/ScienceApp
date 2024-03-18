package com.brody.arxiv.designsystem.ui.appbar

import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.brody.arxiv.designsystem.theme.OnSurface
import com.brody.arxiv.designsystem.theme.OnSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArxivTopAppBar(
    @StringRes titleRes: Int? = null,
    title: String? = null,
    navigationIcon: ImageVector? = null,
    navigationIconContentDescription: String? = null,
    actionIcons: List<ImageVector>?,
    actionIconContentDescription: String? = null,
    modifier: Modifier = Modifier,
    colors: TopAppBarColors = ArxivTopAppBarColors,
    onNavigationClick: () -> Unit = {},
    onActionClicks: List<() -> Unit>? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {

    TopAppBar(
        modifier = modifier,
        title = {
            val text = titleRes?.let { stringResource(id = titleRes) } ?: title
            if (text != null) {
                Text(text = text)
            }
        },
        navigationIcon = {
            CompositionLocalProvider(LocalContentColor provides OnSurface) {
                if (navigationIcon != null) {
                    IconButton(onClick = onNavigationClick) {
                        Icon(
                            imageVector = navigationIcon,
                            contentDescription = navigationIconContentDescription
                        )
                    }
                }
            }
        },
        actions = {
            CompositionLocalProvider(LocalContentColor provides OnSurfaceVariant) {
                actionIcons?.forEachIndexed { i, icon ->
                    IconButton(onClick = onActionClicks?.get(i) ?: {}) {
                        Icon(
                            imageVector = icon,
                            contentDescription = actionIconContentDescription,
                        )
                    }
                }
            }
        },
        colors = colors,
        scrollBehavior = scrollBehavior,
    )
}

val ArxivTopAppBarColors: TopAppBarColors
    @Composable get() = TopAppBarDefaults.topAppBarColors(
        scrolledContainerColor = MaterialTheme.colorScheme.surface,
        containerColor = MaterialTheme.colorScheme.background,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )