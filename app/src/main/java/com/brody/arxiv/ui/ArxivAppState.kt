package com.brody.arxiv.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.brody.arxiv.core.network.util.NetworkMonitor
import com.brody.arxiv.navigation.base.AppComposeNavigator
import com.brody.arxiv.navigation.base.ArxivComposeNavigator
import com.brody.arxiv.onboarding.presentation.ui.navigation.navigateToOnboarding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberArxivAppState(
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): ArxivAppState {
//    NavigationTrackingSideEffect(navController)
    return remember(
        navController,
        coroutineScope,
        networkMonitor
    ) {
        ArxivAppState(
            navController,
            coroutineScope,
            networkMonitor
        )
    }
}

@Stable
class ArxivAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor
) {
    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    fun navigateToOnboarding() = navController.navigateToOnboarding()
}