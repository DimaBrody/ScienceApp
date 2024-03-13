package com.brody.arxiv.features.explore.presentation.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val EXPLORE_ROUTE = "explore_route"

fun NavController.navigateToExplore(navOptions: NavOptions) = navigate(EXPLORE_ROUTE, navOptions)

fun NavGraphBuilder.exploreScreen(

) {
    composable(route = EXPLORE_ROUTE) {

    }
}
