package com.brody.arxiv.features.details.presentation.navigation

import android.net.Uri
import androidx.compose.material3.Text
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
import kotlinx.serialization.encodeToString
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
    onBackClicked: () -> Unit
) {
    composable(route = DETAILS_ROUTE_FULL,
        arguments = listOf(navArgument("paperModelJson") {
            type = NavType.StringType
        }),
        enterTransition = {
//            when (initialState.destination.route) {
//                DETAILS_ROUTE ->
//            slideIntoContainer(
//                AnimatedContentTransitionScope.SlideDirection.Left,
//                animationSpec = tween(700)
//            )
            scaleIntoContainer()

//                else -> null
//            }
        },
        exitTransition = {
//            when (targetState.destination.route) {
//                DETAILS_ROUTE ->
//            slideOutOfContainer(
//                AnimatedContentTransitionScope.SlideDirection.Left,
//                animationSpec = tween(700)
//            )
            scaleOutOfContainer(direction = ScaleTransitionDirection.INWARDS)
//
//                else -> null
//            }?
        },
        popEnterTransition = {
//            when (initialState.destination.route) {
//                DETAILS_ROUTE ->
//            slideIntoContainer(
//                AnimatedContentTransitionScope.SlideDirection.Right,
//                animationSpec = tween(700)
//            )
            scaleIntoContainer(direction = ScaleTransitionDirection.OUTWARDS)

//                else -> null
//            }
        },
        popExitTransition = {
//            when (targetState.destination.route) {
//                DETAILS_ROUTE ->
//            slideOutOfContainer(
//                AnimatedContentTransitionScope.SlideDirection.Right,
//                animationSpec = tween(700)
//            )
            scaleOutOfContainer()

//                else -> null
//            }
        }) { backStackEntry ->

        val paperModelJson = Uri.decode(backStackEntry.arguments?.getString("paperModelJson"))
        val paperModel = Json.decodeFromString(SaveablePaperDataModel.serializer(), paperModelJson)

        DetailsScreen(
            detailsUiModel = paperModel.toPresentationModel(),
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
