package com.brody.arxiv.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.brody.arxiv.core.network.util.NetworkMonitor
import com.brody.arxiv.designsystem.ui.background.ArxivBackground
import com.brody.arxiv.navigation.ArxivNavHost
import com.brody.arxiv.navigation.base.ArxivComposeNavigator

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun ArxivApp(
    isShowOnboarding: Boolean,
    appState: ArxivAppState = rememberArxivAppState(),
) {
//    LaunchedEffect(Unit) {
//        arxivNavigator.handleNavigationCommands(appState.navController)
//    }

    val currentViewModelStoreOwner = LocalViewModelStoreOwner.current
    val currentLifecycleOwner = LocalLifecycleOwner.current

    ArxivBackground {
        ArxivNavHost(
            appState = appState,
            isShowOnboarding = isShowOnboarding,
            viewModelStoreOwner = currentViewModelStoreOwner,
            lifecycleOwner = currentLifecycleOwner
        )
    }
}