package com.brody.arxiv.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.NavHost
import com.brody.arxiv.features.details.presentation.navigation.detailsScreen
import com.brody.arxiv.features.onboarding.presentation.ui.navigation.ONBOARDING_ROUTE
import com.brody.arxiv.features.explore.presentation.ui.navigation.exploreScreen
import com.brody.arxiv.features.onboarding.presentation.ui.navigation.onboardingScreen
import com.brody.arxiv.features.papers.presentation.ui.navigation.papersScreen
import com.brody.arxiv.features.saved.presentation.ui.navigation.savedScreen
import com.brody.arxiv.features.settings.presentation.navigation.settingsScreen
import com.brody.arxiv.features.summary.presentation.navigation.summaryScreen
import com.brody.arxiv.ui.ArxivAppState
import com.brody.arxiv.ui.MAIN_ROUTE
import com.brody.arxiv.ui.mainScreen

@Composable
fun ArxivNavHost(
    appState: ArxivAppState,
    isShowOnboarding: Boolean,
    deepLinkRoute: String? = null,
) {
    val navHostController = appState.navController
    NavHost(
        navController = navHostController,
        startDestination = deepLinkRoute ?: getStartDestination(isShowOnboarding)
    ) {
        onboardingScreen(
            onCompleteClick = appState::navigateToMain
        )

        mainScreen(appState) { scrollListener ->
            papersScreen(
                toolbarActionFlow = appState.topAppBarActionsFlow,
                onScrollListener = scrollListener,
                onPaperClicked = appState::navigateToDetails
            )
            exploreScreen(
                searchFlow = appState.searchFlow,
                scrollListener = scrollListener
            )
            savedScreen(
                scrollListener = scrollListener,
                onPaperClicked = appState::navigateToDetails
            )
            settingsScreen(
                scrollListener = scrollListener
            )
        }

        detailsScreen(
            onSummaryClicked = appState::navigateToSummary,
            onBackClicked = navHostController::navigateUp
        )
        summaryScreen(appState::navigateBackSummary)
    }
}

private fun getStartDestination(isShowOnboarding: Boolean) =
    if (isShowOnboarding) ONBOARDING_ROUTE else MAIN_ROUTE