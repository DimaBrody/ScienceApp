package com.brody.arxiv.features.explore.presentation.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.brody.arxiv.core.common.typealiases.ScrollListener
import com.brody.arxiv.features.explore.presentation.ui.ExploreScreen
import com.brody.arxiv.shared.search.models.presentation.SearchState
import kotlinx.coroutines.flow.StateFlow

const val EXPLORE_ROUTE = "explore_route"

fun NavController.navigateToExplore(navOptions: NavOptions) = navigate(EXPLORE_ROUTE, navOptions)

fun NavGraphBuilder.exploreScreen(
    searchFlow: StateFlow<SearchState>,
    scrollListener: ScrollListener
) {
    composable(route = EXPLORE_ROUTE) {
        LaunchedEffect(Unit) {
            scrollListener.invoke(0)
        }
        ExploreScreen(searchFlow)
    }
}
