package com.brody.arxiv.features.details.presentation.navigation

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
import com.brody.arxiv.features.details.presentation.DetailsScreen
import com.brody.arxiv.features.details.presentation.models.toPresentationModel
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import kotlinx.serialization.json.Json

private const val DETAILS_ROUTE = "details_route"

private const val DETAILS_ROUTE_FULL = "$DETAILS_ROUTE/{paperModelJson}"

fun NavController.navigateToDetails(
    paperModel: SaveablePaperDataModel
) {
    val paperModelJson = Json.encodeToString(SaveablePaperDataModel.serializer(), paperModel)
    val encodedPaperModelJson = Uri.encode(paperModelJson)
    navigate("$DETAILS_ROUTE/$encodedPaperModelJson") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.detailsScreen(
    onSummaryClicked: (SaveablePaperDataModel) -> Unit,
    onBackClicked: () -> Unit
) {
    composable(route = DETAILS_ROUTE_FULL,
        arguments = listOf(navArgument("paperModelJson") {
            type = NavType.StringType
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

        val paperModelJson = Uri.decode(backStackEntry.arguments?.getString("paperModelJson"))
        val paperModel = Json.decodeFromString(SaveablePaperDataModel.serializer(), paperModelJson)

        DetailsScreen(
            saveableDataModel = paperModel,
            onSummaryClicked = onSummaryClicked,
            onBackClicked = onBackClicked
        )
    }
}


//enterTransition = {
//    scaleIntoContainer()
//},
//exitTransition = {
//    scaleOutOfContainer(direction = ScaleTransitionDirection.INWARDS)
//},
//popEnterTransition = {
//    scaleIntoContainer(direction = ScaleTransitionDirection.OUTWARDS)
//},
//popExitTransition = {
//    scaleOutOfContainer()
//}
