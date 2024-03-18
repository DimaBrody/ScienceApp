package com.brody.arxiv.features.settings.presentation.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.brody.arxiv.core.common.typealiases.ScrollListener
import com.brody.arxiv.features.settings.presentation.SettingsScreen

const val SETTINGS_ROUTE = "settings_route"

fun NavController.navigateToSettings(navOptions: NavOptions) = navigate(SETTINGS_ROUTE, navOptions)

fun NavGraphBuilder.settingsScreen(
    scrollListener: ScrollListener
) {
    composable(route = SETTINGS_ROUTE) {
        LaunchedEffect(Unit) {
            scrollListener.invoke(0)
        }
        SettingsScreen()
    }
}
