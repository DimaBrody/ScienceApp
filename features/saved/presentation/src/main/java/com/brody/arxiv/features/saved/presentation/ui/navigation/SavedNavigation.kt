package com.brody.arxiv.features.saved.presentation.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val SAVED_ROUTE = "saved_route"

fun NavController.navigateToSaved(navOptions: NavOptions) = navigate(SAVED_ROUTE, navOptions)

fun NavGraphBuilder.savedScreen(

) {
    composable(route = SAVED_ROUTE) {
        SavedScreen()
    }
}
