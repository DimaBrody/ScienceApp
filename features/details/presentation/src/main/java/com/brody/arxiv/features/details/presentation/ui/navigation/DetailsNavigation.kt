package com.brody.arxiv.features.details.presentation.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.brody.arxiv.designsystem.animations.ScaleTransitionDirection
import com.brody.arxiv.designsystem.animations.scaleIntoContainer
import com.brody.arxiv.designsystem.animations.scaleOutOfContainer

const val DETAILS_ROUTE = "details_route"

fun NavController.navigateToDetails() = navigate(DETAILS_ROUTE)

fun NavGraphBuilder.detailsScreen(

) {
    composable(route = DETAILS_ROUTE,
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
        }) {
        Text(text = "Details")
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
