package com.brody.arxiv.ui

import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.trace
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.brody.arxiv.R
import com.brody.arxiv.designsystem.ui.components.ArxivNavigationBar
import com.brody.arxiv.designsystem.ui.components.ArxivNavigationBarItem
import com.brody.arxiv.designsystem.ui.components.ArxivTopAppBar
import com.brody.arxiv.designsystem.ui.icons.ArxivIcons
import com.brody.arxiv.features.explore.presentation.ui.navigation.EXPLORE_ROUTE
import com.brody.arxiv.features.explore.presentation.ui.navigation.navigateToExplore
import com.brody.arxiv.features.onboarding.presentation.ui.navigation.ONBOARDING_ROUTE
import com.brody.arxiv.features.papers.presentation.ui.navigation.PAPERS_ROUTE
import com.brody.arxiv.features.papers.presentation.ui.navigation.navigateToPapers
import com.brody.arxiv.features.saved.presentation.ui.navigation.SAVED_ROUTE
import com.brody.arxiv.features.saved.presentation.ui.navigation.navigateToSaved
import com.brody.arxiv.features.settings.presentation.ui.navigation.SETTINGS_ROUTE
import com.brody.arxiv.features.settings.presentation.ui.navigation.navigateToSettings
import com.brody.arxiv.navigation.BottomNavDestination


const val MAIN_ROUTE = "main_route"

fun NavController.navigateToMain() = navigate(MAIN_ROUTE) {
    popUpTo(ONBOARDING_ROUTE) { inclusive = true }
    launchSingleTop = true
}

fun NavGraphBuilder.mainScreen(
    appState: ArxivAppState, onNavGraph: NavGraphBuilder.() -> Unit
) {
    composable(route = MAIN_ROUTE) {
        MainScreen(appState = appState) {
            onNavGraph()
        }
    }
}

@Composable
fun MainScreen(
    appState: ArxivAppState, onNavGraph: NavGraphBuilder.() -> Unit
) {
    val navController = rememberNavController()

    val currentDestination: NavDestination? =
        navController.currentBackStackEntryAsState().value?.destination

    val currentBottomNavDestination: BottomNavDestination? = when (currentDestination?.route) {
        PAPERS_ROUTE -> BottomNavDestination.PAPERS
        EXPLORE_ROUTE -> BottomNavDestination.EXPLORE
        SAVED_ROUTE -> BottomNavDestination.SAVED
        SETTINGS_ROUTE -> BottomNavDestination.SETTINGS
        else -> null
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (appState.shouldShowBottomBar) {
                ArxivBottomBar(
                    destinations = BottomNavDestination.entries, onNavigateToDestination = {
                        navigateToBottomNavDestination(navController, it)
                    }, currentDestination = currentDestination
                )
            }
        },
        topBar = {
            if (currentBottomNavDestination != null) {
                ArxivTopAppBar(
                    titleRes = currentBottomNavDestination.titleTextId,
                    navigationIcon = null,
                    navigationIconContentDescription = stringResource(
                        id = R.string.toolbar_nav_description,
                    ),
                    actionIcons = currentBottomNavDestination.toolbarSecondaryButtons?.map { it.first },
                    actionIconContentDescription = stringResource(
                        id = R.string.toolbar_support_description,
                    ),
                    onActionClicks = currentBottomNavDestination.toolbarSecondaryButtons?.map { pair ->
                        { appState.navigateToolbarAction(pair.second) }
                    },
                    onNavigationClick = { navController.popBackStack() },
                    scrollBehavior = scrollBehavior
                )
            }
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal,
                    ),
                ),
        ) {
            NavHost(navController = navController,
                startDestination = PAPERS_ROUTE,
                enterTransition = {
                    fadeIn(tween(300))
                },
                exitTransition = {
                    fadeOut(tween(150))
                },
                popEnterTransition = {
                    fadeIn(tween(300))
                },
                popExitTransition = {
                    fadeOut(tween(150))
                }) {
                onNavGraph()
            }
        }
    }
}

@Composable
private fun ArxivBottomBar(
    destinations: List<BottomNavDestination>,
    onNavigateToDestination: (BottomNavDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    ArxivNavigationBar(
        modifier = modifier,

        ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            ArxivNavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = destination.unselectedIcon,
                        contentDescription = null,
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(destination.titleTextId)) },
            )
        }
    }
}

fun navigateToBottomNavDestination(
    navController: NavController, bottomNavDestination: BottomNavDestination
) {
    trace("Navigation: ${bottomNavDestination.name}") {
        val bottomNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true

            anim {
                enter = 0
                exit = 0
                popEnter = 0
                popExit = 0
            }
        }

        when (bottomNavDestination) {
            BottomNavDestination.PAPERS -> navController.navigateToPapers(bottomNavOptions)
            BottomNavDestination.EXPLORE -> navController.navigateToExplore(bottomNavOptions)
            BottomNavDestination.SAVED -> navController.navigateToSaved(bottomNavOptions)
            BottomNavDestination.SETTINGS -> navController.navigateToSettings(bottomNavOptions)
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: BottomNavDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false