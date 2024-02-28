package com.brody.arxiv.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.brody.arxiv.navigation.base.AppComposeNavigator
import com.brody.arxiv.onboarding.presentation.ui.navigation.ONBOARDING_ROUTE
import com.brody.arxiv.onboarding.presentation.ui.navigation.onboardingScreen

@Composable
fun ArxivNavHost(
    navHostController: NavHostController,
    isShowOnboarding: Boolean
) {
    NavHost(
        navController = navHostController,
        startDestination = getStartDestination(isShowOnboarding)
    ) {
        onboardingScreen()
    }
}

private fun getStartDestination(isShowOnboarding: Boolean) =
    if (isShowOnboarding) ONBOARDING_ROUTE else ONBOARDING_ROUTE