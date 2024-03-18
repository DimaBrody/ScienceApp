package com.brody.arxiv.designsystem.ui.refresh

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BoxScope.ArxivRefresh(
    isRefreshing: Boolean,
    refreshState: PullRefreshState
) {

//    CompositionLocalProvider(LocalElevationOverlay provides null) {
        PullRefreshIndicator(
            refreshing = isRefreshing, state = refreshState,
            modifier = Modifier
                .align(
                    Alignment.TopCenter
                ),
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            scale = true,
        )
//    }

}