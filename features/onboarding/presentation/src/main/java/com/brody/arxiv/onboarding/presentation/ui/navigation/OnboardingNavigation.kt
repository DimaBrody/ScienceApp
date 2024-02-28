package com.brody.arxiv.onboarding.presentation.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.brody.arxiv.onboarding.presentation.ui.OnboardingRoute

const val ONBOARDING_ROUTE = "onboarding_route"

fun NavController.navigateToOnboarding() = navigate(ONBOARDING_ROUTE)

fun NavGraphBuilder.onboardingScreen() {
    composable(route = ONBOARDING_ROUTE) {
        OnboardingRoute()
    }
}
