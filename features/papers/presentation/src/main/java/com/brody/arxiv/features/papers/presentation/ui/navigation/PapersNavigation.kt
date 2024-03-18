package com.brody.arxiv.features.papers.presentation.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.brody.arxiv.core.common.actions.MainToolbarAction
import com.brody.arxiv.core.common.typealiases.ScrollListener
import com.brody.arxiv.features.papers.presentation.PapersScreen
import com.brody.arxiv.shared.saved.models.domain.OnPaperClicked
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import kotlinx.coroutines.flow.SharedFlow

const val PAPERS_ROUTE = "papers_route"

fun NavController.navigateToPapers(navOptions: NavOptions) = navigate(PAPERS_ROUTE, navOptions)

fun NavGraphBuilder.papersScreen(
    toolbarActionFlow: SharedFlow<MainToolbarAction>,
    onScrollListener: ScrollListener,
    onPaperClicked: OnPaperClicked
) {
    composable(route = PAPERS_ROUTE) {
        PapersScreen(
            toolbarActionFlow = toolbarActionFlow,
            scrollListener = onScrollListener,
            onPaperClicked = onPaperClicked
        )
    }

}
