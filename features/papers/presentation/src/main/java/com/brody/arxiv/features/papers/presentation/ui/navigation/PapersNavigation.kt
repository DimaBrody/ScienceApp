package com.brody.arxiv.features.papers.presentation.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.brody.arxiv.core.common.actions.MainToolbarAction
import com.brody.arxiv.features.papers.presentation.PapersScreen
import kotlinx.coroutines.flow.SharedFlow

const val PAPERS_ROUTE = "papers_route"

fun NavController.navigateToPapers(navOptions: NavOptions) = navigate(PAPERS_ROUTE, navOptions)

fun NavGraphBuilder.papersScreen(
    toolbarActionFlow: SharedFlow<MainToolbarAction>,
    onDetailsClick: () -> Unit
) {
    composable(route = PAPERS_ROUTE) {
        PapersScreen(toolbarActionFlow)
    }

}
