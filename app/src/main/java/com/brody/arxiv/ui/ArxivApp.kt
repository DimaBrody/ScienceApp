package com.brody.arxiv.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brody.arxiv.core.network.util.NetworkMonitor
import com.brody.arxiv.designsystem.ui.background.ArxivBackground
import com.brody.arxiv.navigation.ArxivNavHost
import com.brody.arxiv.navigation.base.ArxivComposeNavigator

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun ArxivApp(
    networkMonitor: NetworkMonitor,
    isShowOnboarding: Boolean,
    appState: ArxivAppState = rememberArxivAppState(
        networkMonitor = networkMonitor
    ),
) {
//    LaunchedEffect(Unit) {
//        arxivNavigator.handleNavigationCommands(appState.navController)
//    }
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()

    ArxivBackground {
        ArxivNavHost(appState = appState, isShowOnboarding)
    }
}