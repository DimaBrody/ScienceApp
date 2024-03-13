package com.brody.arxiv.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.brody.arxiv.features.details.presentation.ui.navigation.detailsScreen
import com.brody.arxiv.features.onboarding.presentation.ui.navigation.ONBOARDING_ROUTE
import com.brody.arxiv.features.explore.presentation.ui.navigation.exploreScreen
import com.brody.arxiv.features.onboarding.presentation.ui.navigation.onboardingScreen
import com.brody.arxiv.features.papers.presentation.ui.navigation.papersScreen
import com.brody.arxiv.features.saved.presentation.ui.navigation.savedScreen
import com.brody.arxiv.features.settings.presentation.ui.navigation.settingsScreen
import com.brody.arxiv.ui.ArxivAppState
import com.brody.arxiv.ui.MAIN_ROUTE
import com.brody.arxiv.ui.mainScreen

@Composable
fun ArxivNavHost(
    appState: ArxivAppState,
    isShowOnboarding: Boolean
) {
    val navHostController = appState.navController
    NavHost(
        navController = navHostController,
        startDestination = getStartDestination(isShowOnboarding)
    ) {
        onboardingScreen(
            onCompleteClick = appState::navigateToMain
        )

        mainScreen(appState) {
            papersScreen(appState.toolbarActionsFlow) {
                appState.navigateToDetails()
            }
            exploreScreen()
            savedScreen()
            settingsScreen()
        }

        detailsScreen()
    }
}

private fun getStartDestination(isShowOnboarding: Boolean) =
    if (isShowOnboarding) ONBOARDING_ROUTE else MAIN_ROUTE