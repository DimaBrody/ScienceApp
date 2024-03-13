package com.brody.arxiv.designsystem.ui.dialogs

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.brody.arxiv.designsystem.animations.scaleIntoContainer
import com.brody.arxiv.designsystem.animations.scaleOutOfContainer


@Composable
fun AnimatedFullScreenDialog(
    expanded: Boolean,
    onDismissRequest: (() -> Unit)? = null,
    enter: EnterTransition = scaleIntoContainer(),
    exit: ExitTransition = scaleOutOfContainer(),
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val expandedState = remember { MutableTransitionState(false) }
    expandedState.targetState = expanded

    if (expandedState.currentState || expandedState.targetState || !expandedState.isIdle) {
        DialogFullScreen(
            onDismissRequest = onDismissRequest,
        ) {
            AnimatedVisibility(
                visibleState = expandedState,
                enter = enter,
                exit = exit,
                modifier = modifier,
                content = content
            )
        }
    }
}


@Composable
fun DialogFullScreen(
    onDismissRequest: (() -> Unit)?,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest ?: {}, properties = DialogProperties(
        dismissOnBackPress = properties.dismissOnBackPress,
        dismissOnClickOutside = properties.dismissOnClickOutside,
        securePolicy = properties.securePolicy,
        usePlatformDefaultWidth = true,
        decorFitsSystemWindows = false
    ), content = {
        val activityWindow = getActivityWindow()
        val dialogWindow = getDialogWindow()
        SideEffect {
            if (activityWindow != null && dialogWindow != null) {
                val attributes = WindowManager.LayoutParams()
                attributes.copyFrom(activityWindow.attributes)
                attributes.type = dialogWindow.attributes.type
                dialogWindow.attributes = attributes
//                parentView.layoutParams = FrameLayout.LayoutParams(
//                    activityWindow.decorView.width, activityWindow.decorView.height
//                )
            }
        }

        content()
    })
}


// Window utils
@Composable
fun getDialogWindow(): Window? = (LocalView.current.parent as? DialogWindowProvider)?.window

@Composable
fun getActivityWindow(): Window? = LocalView.current.context.getActivityWindow()

private tailrec fun Context.getActivityWindow(): Window? = when (this) {
    is Activity -> window
    is ContextWrapper -> baseContext.getActivityWindow()
    else -> null
}