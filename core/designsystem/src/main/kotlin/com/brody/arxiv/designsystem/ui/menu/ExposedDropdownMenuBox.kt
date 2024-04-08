package com.brody.arxiv.designsystem.ui.menu

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.toSize
import kotlin.math.max
import kotlin.math.roundToInt

@ExperimentalMaterial3Api
@Composable
fun ArxivDropdownMenuBox(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ExposedDropdownMenuBoxScope.() -> Unit
) {
    val config = LocalConfiguration.current
    val view = LocalView.current
    val density = LocalDensity.current

    val verticalMargin = with(density) { 0 }

    var anchorCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var anchorWidth by remember { mutableIntStateOf(0) }
    var menuMaxHeight by remember { mutableIntStateOf(0) }

    val focusRequester = remember { FocusRequester() }
    val expandedDescription = "exp"
    val collapsedDescription = "coll"

    val scope = remember(expanded, onExpandedChange, config, view, density) {
        object : ExposedDropdownMenuBoxScope() {
            override fun Modifier.menuAnchor(): Modifier = this
                .onGloballyPositioned {
                    anchorCoordinates = it
                    anchorWidth = it.size.width
                    menuMaxHeight = calculateMaxHeight(
                        windowBounds = view.rootView.getWindowBounds(),
                        anchorBounds = anchorCoordinates.getAnchorBounds(),
                        verticalMargin = verticalMargin,
                    )
                }
                .expandable(
                    expanded = expanded,
                    onExpandedChange = { onExpandedChange(!expanded) },
                    expandedDescription = expandedDescription,
                    collapsedDescription = collapsedDescription,
                )
                .focusRequester(focusRequester)

            override fun Modifier.exposedDropdownSize(matchTextFieldWidth: Boolean): Modifier =
                layout { measurable, constraints ->
                    val menuWidth = constraints.constrainWidth(anchorWidth)
                    val menuConstraints = constraints.copy(
                        maxHeight = constraints.constrainHeight(menuMaxHeight),
                        minWidth = if (matchTextFieldWidth) menuWidth else constraints.minWidth,
                        maxWidth = if (matchTextFieldWidth) menuWidth else constraints.maxWidth,
                    )
                    val placeable = measurable.measure(menuConstraints)
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, 0)
                    }
                }
        }
    }

    Box(modifier) {
        scope.content()
    }

    if (expanded) {
        SoftKeyboardListener(view, density) {
            menuMaxHeight = calculateMaxHeight(
                windowBounds = view.rootView.getWindowBounds(),
                anchorBounds = anchorCoordinates.getAnchorBounds(),
                verticalMargin = verticalMargin,
            )
        }
    }

    SideEffect {
        if (expanded) focusRequester.requestFocus()
    }
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
private fun Modifier.expandable(
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    expandedDescription: String,
    collapsedDescription: String,
) = pointerInput(onExpandedChange) {
    awaitEachGesture {
        // Must be PointerEventPass.Initial to observe events before the text field consumes them
        // in the Main pass
        awaitFirstDown(pass = PointerEventPass.Initial)
        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
        if (upEvent != null) {
            onExpandedChange()
        }
    }
}.semantics {
    stateDescription = if (expanded) expandedDescription else collapsedDescription
    role = Role.DropdownList
    onClick {
        onExpandedChange()
        true
    }
}

private fun calculateMaxHeight(
    windowBounds: Rect,
    anchorBounds: Rect?,
    verticalMargin: Int,
): Int {
    anchorBounds ?: return 0

    val marginedWindowTop = windowBounds.top + verticalMargin
    val marginedWindowBottom = windowBounds.bottom - verticalMargin
    val availableHeight =
        if (anchorBounds.top > windowBounds.bottom || anchorBounds.bottom < windowBounds.top) {
            (marginedWindowBottom - marginedWindowTop).roundToInt()
        } else {
            val heightAbove = anchorBounds.top - marginedWindowTop
            val heightBelow = marginedWindowBottom - anchorBounds.bottom
            max(heightAbove, heightBelow).roundToInt()
        }

    return max(availableHeight, 0)
}

private fun View.getWindowBounds(): Rect = android.graphics.Rect().let {
    this.getWindowVisibleDisplayFrame(it)
    it.toComposeRect()
}

private fun LayoutCoordinates?.getAnchorBounds(): Rect {
    // Don't use `boundsInWindow()` because it can report 0 when the window is animating/resizing
    return if (this == null) Rect.Zero else Rect(positionInWindow(), size.toSize())
}

@Composable
private fun SoftKeyboardListener(
    view: View,
    density: Density,
    onKeyboardVisibilityChange: () -> Unit,
) {
    // It would be easier to listen to WindowInsets.ime, but that doesn't work with
    // `setDecorFitsSystemWindows(window, true)`. Instead, listen to the view tree's global layout.
    DisposableEffect(view, density) {
        val listener =
            object : View.OnAttachStateChangeListener, ViewTreeObserver.OnGlobalLayoutListener {
                private var isListeningToGlobalLayout = false
                init {
                    view.addOnAttachStateChangeListener(this)
                    registerOnGlobalLayoutListener()
                }
                override fun onViewAttachedToWindow(p0: View) = registerOnGlobalLayoutListener()
                override fun onViewDetachedFromWindow(p0: View) = unregisterOnGlobalLayoutListener()
                override fun onGlobalLayout() = onKeyboardVisibilityChange()
                private fun registerOnGlobalLayoutListener() {
                    if (isListeningToGlobalLayout || !view.isAttachedToWindow) return
                    view.viewTreeObserver.addOnGlobalLayoutListener(this)
                    isListeningToGlobalLayout = true
                }
                private fun unregisterOnGlobalLayoutListener() {
                    if (!isListeningToGlobalLayout) return
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    isListeningToGlobalLayout = false
                }
                fun dispose() {
                    unregisterOnGlobalLayoutListener()
                    view.removeOnAttachStateChangeListener(this)
                }
            }

        onDispose { listener.dispose() }
    }
}