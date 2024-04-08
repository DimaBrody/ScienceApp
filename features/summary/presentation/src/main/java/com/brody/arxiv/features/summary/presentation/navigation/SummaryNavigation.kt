package com.brody.arxiv.features.summary.presentation.navigation

import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.brody.arxiv.designsystem.animations.ScaleTransitionDirection
import com.brody.arxiv.designsystem.animations.scaleIntoContainer
import com.brody.arxiv.designsystem.animations.scaleOutOfContainer
import com.brody.arxiv.features.summary.navigation.SUMMARY_ROUTE
import com.brody.arxiv.features.summary.navigation.createNavLink
import com.brody.arxiv.features.summary.presentation.ui.screen.SummaryScreen
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import kotlinx.serialization.json.Json


const val PAPER_MODEL = "paperModelJson"
const val FROM_DEEP_LINK = "fromDeepLink"

const val PAPER_ROUTE_FULL = "$SUMMARY_ROUTE/{$PAPER_MODEL}/{$FROM_DEEP_LINK}"

fun NavController.navigateToSummary(
    paperModel: SaveablePaperDataModel,
    isDeepLink: Boolean = false
) {
    navigate(createNavLink(paperModel, isDeepLink)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.summaryScreen(
    onBackClicked: (isDeepLink: Boolean) -> Unit
) {
    composable(route = PAPER_ROUTE_FULL, arguments = listOf(navArgument(PAPER_MODEL) {
        type = NavType.StringType
    }, navArgument(FROM_DEEP_LINK) {
        type = NavType.BoolType
    }),
        enterTransition = {
            fadeIn(tween(300))
        },
        exitTransition = {
            fadeOut(tween(150))
        },
        popEnterTransition = {
            fadeIn(tween(300))
        },
        popExitTransition = {
            fadeOut(tween(150))
        }) { backStackEntry ->
        val paperModelJson = Uri.decode(backStackEntry.arguments?.getString(PAPER_MODEL))
        val paperModel = Json.decodeFromString(SaveablePaperDataModel.serializer(), paperModelJson)

        val isDeepLink: Boolean = backStackEntry.arguments?.getBoolean(FROM_DEEP_LINK) ?: false

        SummaryScreen(
            saveableDataModel = paperModel,
            onBackClicked = { onBackClicked(isDeepLink) }
        )
    }
}
