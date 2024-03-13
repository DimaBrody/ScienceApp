package com.brody.arxiv.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.brody.arxiv.core.common.actions.MainToolbarAction
import com.brody.arxiv.core.network.util.NetworkMonitor
import com.brody.arxiv.features.details.presentation.ui.navigation.navigateToDetails
import com.brody.arxiv.features.onboarding.presentation.ui.navigation.navigateToOnboarding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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

    val shouldShowBottomBar: Boolean
        get() = true

    private val _toolbarActionsFlow: MutableSharedFlow<MainToolbarAction> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val toolbarActionsFlow: SharedFlow<MainToolbarAction>
        get() = _toolbarActionsFlow

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination
//
//    val currentBottomNavDestination: BottomNavDestination?
//        @Composable get() = when (currentDestination?.route) {
//            PAPERS_ROUTE -> BottomNavDestination.PAPERS
//            EXPLORE_ROUTE -> BottomNavDestination.EXPLORE
//            SAVED_ROUTE -> BottomNavDestination.SAVED
//            SETTINGS_ROUTE -> BottomNavDestination.SETTINGS
//            else -> null
//        }.also { Log.d("ARXIV_H", currentDestination?.route.toString()) }

//    fun navigateToBottomNavDestination(bottomNavDestination: BottomNavDestination) {
//        trace("Navigation: ${bottomNavDestination.name}") {
//            val bottomNavOptions = navOptions {
//                // Pop up to the start destination of the graph to
//                // avoid building up a large stack of destinations
//                // on the back stack as users select items
//                popUpTo(navController.graph.findStartDestination().id) {
//                    saveState = true
//                }
//                // Avoid multiple copies of the same destination when
//                // reselecting the same item
//                launchSingleTop = true
//                // Restore state when reselecting a previously selected item
//                restoreState = true
//            }
//
//            when (bottomNavDestination) {
//                BottomNavDestination.PAPERS -> navController.navigateToPapers(bottomNavOptions)
//                BottomNavDestination.EXPLORE -> navController.navigateToExplore(bottomNavOptions)
//                BottomNavDestination.SAVED -> navController.navigateToSaved(bottomNavOptions)
//                BottomNavDestination.SETTINGS -> navController.navigateToSettings(bottomNavOptions)
//            }
//        }
//    }

    fun navigateToOnboarding() = navController.navigateToOnboarding()

    fun navigateToMain() = navController.navigateToMain()

    fun navigateToDetails() = navController.navigateToDetails()

    fun navigateToolbarAction(action: MainToolbarAction) {
        _toolbarActionsFlow.tryEmit(action)
    }


//    fun navigateToMain() = navController.navigateToOnboarding()
}