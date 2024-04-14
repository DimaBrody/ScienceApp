package com.brody.arxiv.features.summary.presentation.ui.screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brody.arxiv.core.common.extensions.toFloat
import com.brody.arxiv.core.common.extensions.toInt
import com.brody.arxiv.core.common.ui.showToast
import com.brody.arxiv.designsystem.annotations.DefaultPreview
import com.brody.arxiv.designsystem.theme.ArxivTheme
import com.brody.arxiv.designsystem.theme.HelperUnderTextTheme
import com.brody.arxiv.designsystem.theme.OnSurfaceVariant
import com.brody.arxiv.designsystem.theme.PrimaryHeaderText
import com.brody.arxiv.designsystem.ui.appbar.ArxivTopAppBar
import com.brody.arxiv.designsystem.ui.appbar.IgnorantPinnedScrollBehavior
import com.brody.arxiv.designsystem.ui.buttons.ArxivSegmentedButton
import com.brody.arxiv.designsystem.ui.buttons.PrimaryButton
import com.brody.arxiv.designsystem.ui.buttons.SecondaryButton
import com.brody.arxiv.designsystem.ui.components.ArxivNavigationBar
import com.brody.arxiv.designsystem.ui.icons.ArxivIcons
import com.brody.arxiv.designsystem.ui.list.composableListenerForScrollState
import com.brody.arxiv.features.summary.presentation.R
import com.brody.arxiv.features.summary.presentation.models.DEFAULT_CHUNK_PROMPT
import com.brody.arxiv.features.summary.presentation.models.FINAL_PROMPT_1
import com.brody.arxiv.features.summary.presentation.ui.settings.SummarySettingsAiUiState
import com.brody.arxiv.features.summary.presentation.ui.settings.SummarySettingsDialog
import com.brody.arxiv.features.summary.presentation.utils.toButtonText
import com.brody.arxiv.features.summary.presentation.utils.toDescText
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import com.brody.arxiv.shared.summary.models.domain.SummaryType
//import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SummaryScreen(
    saveableDataModel: SaveablePaperDataModel, onBackClicked: () -> Unit
) {
    SummaryScreenInternal(
        saveableDataModel = saveableDataModel, onBackClicked = onBackClicked
    )
}

@Composable
private fun SummaryScreenInternal(
    saveableDataModel: SaveablePaperDataModel,
    onBackClicked: () -> Unit,
    viewModel: SummaryViewModel = hiltViewModel()
) {

    val scrollState = rememberTopAppBarState()
    val scrollBehavior = IgnorantPinnedScrollBehavior(scrollState)
    val currentModel by viewModel.currentItem.collectAsStateWithLifecycle()
    val summaryState by viewModel.connectSummaryFlow(saveableDataModel)
        .collectAsStateWithLifecycle()

    val isShowDialog = rememberSaveable { mutableStateOf(false) }

    ShowErrorToast(summaryState)

    currentModel?.let { model ->
        Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
            // Add scroll behaviour to top bar
            ArxivTopAppBar(
                navigationIcon = ArxivIcons.ArrowBack,
                onNavigationClick = onBackClicked,
                actionIcons = listOf(ArxivIcons.SettingsActive),
                onActionClicks = listOf {
                    isShowDialog.value = true
                },
                titleRes = R.string.summary,
                scrollBehavior = scrollBehavior
            )
        }, bottomBar = {
            BottomBar(
                summaryState = summaryState,
                settingsAiState = viewModel::settingsAiState::get,
                onStartSummary = {
                    viewModel.startSummaryWork(
                        DEFAULT_CHUNK_PROMPT,
                        FINAL_PROMPT_1
                    )
                },
                onCancelClick = viewModel::cancelWork
            )
        }) { padding ->

            val innerScrollState = composableListenerForScrollState {
                scrollState.contentOffset = (-10) * (it > 0).toFloat()
            }

            val coroutineScope = rememberCoroutineScope()
//            val scrollValue = remember { NonObservableState(0) }

            var scrollUiState: SummaryScrollUiState by remember {
                mutableStateOf(
                    SummaryScrollUiState.Idle
                )
            }

//            val lazyListState = composableScrollListener {
//                if (it - scrollValue.value < 0) {
//                    scrollUiState = SummaryScrollUiState.Idle
//                }
//                scrollValue.update(it)
//            }


            // Automatically scroll to the bottom when new content is added, if shouldScrollToBottom is true
            LaunchedEffect(scrollUiState) {
                when (scrollUiState) {
                    is SummaryScrollUiState.ButtonClickScroll -> {


                        innerScrollState.animateScrollTo(
                            innerScrollState.maxValue,
                            if (summaryState.isIdle()) SpringSpec() else linearAnimSpec
                        )
//                        lazyListState.stopScroll()
//                        lazyListState.animateScrollToItem(index = 2)
                        scrollUiState = SummaryScrollUiState.StickScroll
                    }

                    is SummaryScrollUiState.StickScroll -> {
                        innerScrollState.scrollTo(innerScrollState.maxValue)
//                        innerScrollState.animateScrollTo(innerScrollState.maxValue, linearAnimSpec)
//                        lazyListState.scrollToItem(index = Int.MAX_VALUE)
                    }

                    is SummaryScrollUiState.Idle -> {}
                }
            }

            LaunchedEffect(summaryState) {
                val isStickScroll = scrollUiState is SummaryScrollUiState.StickScroll

                if (summaryState is SummaryUiState.Printing) {
                    if (isStickScroll) {
                        innerScrollState.scrollTo(innerScrollState.maxValue)
//                        lazyListState.scrollToItem(index = Int.MAX_VALUE)
                    }
                }
                if (summaryState is SummaryUiState.Fetched) {
                    if (isStickScroll) {
                        scrollUiState = SummaryScrollUiState.Idle
                    }
                }
            }

            val interactionModifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
//                    onPress = {
//
//                    },
                    onPress = {
                        scrollUiState = SummaryScrollUiState.Idle
                    },
                    onTap = {
                        scrollUiState = SummaryScrollUiState.Idle
                    })
            }
//                .clickable(
//                // Use this to explicitly consume the click event, preventing it from propagating.
//                onClick = {},
//                indication = null,
//                interactionSource = remember { MutableInteractionSource() }
//            )

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(innerScrollState),
//                    verticalArrangement = Arrangement.spacedBy(24.dp),
//                    state = lazyListState
                ) {
//                    item {
                    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        Text(
                            text = model.title, style = MaterialTheme.typography.bodyLarge.copy(
                                color = OnSurfaceVariant,
                                letterSpacing = 0.sp,
                                lineHeight = 28.sp,
                                fontSize = 18.sp
                            ), modifier = Modifier.padding(top = 8.dp)
                        )
                        SummarySegmentedButtons(
                            isStateIdle = summaryState.isIdle(),
                            settingsStateFlow = { viewModel.settingsFlow },
                            existingSummariesFlow = { viewModel.existingSummaries },
                            updateSummaryType = viewModel::updateSummaryType,
                        )
                    }
//                    }

//                    item {
                    Column {
                        Spacer(modifier = Modifier.height(24.dp))
                        SummaryText(summaryState = summaryState)
//                        Spacer(modifier = Modifier.height(12.dp))
                        BulletPointList(
                            text = summaryState.text,
                            modifier = interactionModifier
                        )
                    }
//                    }
//                    item {
//                        Spacer(modifier = Modifier.height(1.dp))
//                    }
                }
                ScrollDownButtonWithAnimation(
                    scrollState = innerScrollState,
                    scrollUiState = scrollUiState,
                ) {
                    scrollUiState = SummaryScrollUiState.ButtonClickScroll
                }

            }
        }

        SummarySettingsDialog(isShowDialog)
    }
}

private val linearAnimSpec: AnimationSpec<Float> by lazy {
    tween(
        durationMillis = 200,
        easing = LinearEasing
    )
}

@Composable
fun SummaryGenerationScreen() {

}

@Composable
fun BulletPointList(
    text: String,
    modifier: Modifier = Modifier
) {
    val sections = text.split("\n\n") // Split text into sections by double newlines
    val paddingValues = PaddingValues(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
    val textPaddingValuesAfterList =
        PaddingValues(start = 0.dp, end = 0.dp, top = 16.dp, bottom = 4.dp)
    val defaultPaddingValues =
        PaddingValues(start = 0.dp, end = 0.dp, top = 4.dp, bottom = 4.dp)

    val numericItemPaddingValues = PaddingValues(
        start = 2.dp,
        end = 2.dp,
        top = 16.dp,
        bottom = 4.dp
    ) // Custom padding for numeric list items

    var isList = false
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        sections.forEach { section ->
            // Split the section by lines to check for bullet points and numeric list items more accurately
            val lines = section.split("\n")
            // Track if we are in a list (bullet or numeric)
            var isFirstElement = true
            lines.forEach { line ->
                when {
                    line.trim().startsWith("-") -> {
                        // Handle bullet list items
                        if (!isList) {
                            isList = true // Start of a new list
                        }
                        RenderBulletListItem(line.trim().drop(2), paddingValues)
                    }

                    line.trim().matches(Regex("^\\d+\\.\\s.*$")) -> {
                        // Handle numeric list items
                        if (!isList) {
                            isList = true // Start of a new list
                        }
                        RenderNumericListItem(line.trim(), numericItemPaddingValues)
                    }

                    else -> {
                        val isListBefore = isList

                        if (isList) {
                            isList = false // End of the list
                        }
                        // Render non-list line as regular text
                        RenderText(
                            line, if (isListBefore || isFirstElement) textPaddingValuesAfterList
                            else defaultPaddingValues
                        )
                    }
                }
                isFirstElement = false
            }
        }
    }
}

// Helper function to render numeric list item
@Composable
fun RenderNumericListItem(item: String, paddingValues: PaddingValues) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
    ) {
        // Assuming you want to render the numeric list item similarly to bullet list items
        Text(
            text = item,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        )
    }
}

// Helper function to render text
@Composable
fun RenderText(text: String, paddingValues: PaddingValues) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues),
        style = MaterialTheme.typography.bodyMedium
    )
}

// Helper function to render bullet list item
@Composable
fun RenderBulletListItem(item: String, paddingValues: PaddingValues) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
    ) {
        Text(
            text = "• ", // Bullet character
            modifier = Modifier.align(alignment = Alignment.Top),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = item.trim(),
            modifier = Modifier.align(alignment = Alignment.Top),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ShowErrorToast(state: SummaryUiState) {
    val currentContext = LocalContext.current

    LaunchedEffect(state) {
        if (state is SummaryUiState.Failed) showToast(currentContext, message = state.text)
    }
}

@Composable
private fun BottomBar(
    summaryState: SummaryUiState,
    settingsAiState: () -> StateFlow<SummarySettingsAiUiState>,
    onStartSummary: () -> Unit,
    onCancelClick: () -> Unit,
) {
    val settingsState by settingsAiState().collectAsStateWithLifecycle()
    val currentContext = LocalContext.current

    ArxivNavigationBar(isCustom = true) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            val buttonModifier = Modifier.fillMaxWidth()
            if (summaryState.isIdle()) {
                PrimaryButton(
                    modifier = buttonModifier, onClick = {
                        if (settingsState is SummarySettingsAiUiState.Fetched) onStartSummary()
                        else {
                            val text = "You have no model selected, go to settings and select one"
                            showToast(currentContext, message = text)
                        }
                    }, text = "Summarize", textColor = MaterialTheme.colorScheme.onPrimary
//                    enabled = settingsState !is SummarySettingsAiUiState.Connecting
                )
            } else {
                CancelButton(
                    modifier = buttonModifier,
                    onCancelClick = onCancelClick,
                )
            }

        }
    }
}

@Composable
private fun SummarySegmentedButtons(
    isStateIdle: Boolean,
    settingsStateFlow: () -> StateFlow<SummarySettingsUiState>,
    existingSummariesFlow: () -> StateFlow<ImmutableList<SummaryType>>,
    updateSummaryType: (SummaryType) -> Unit,
) {
    val settingsState by settingsStateFlow().collectAsStateWithLifecycle()
    val existingSummaries by existingSummariesFlow().collectAsStateWithLifecycle()

    val selectedEntry =
        (settingsState as? SummarySettingsUiState.Fetched)?.summarySettings?.summaryType

    val options = SummaryType.entries

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.alpha(0.38f + isStateIdle.toInt() * 0.62f)
    ) {
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, value ->
//                Log.d("HELLOE", "$value , $existingSummaries, ${ value in existingSummaries}")
                val isCurrentSelected = value == selectedEntry
                ArxivSegmentedButton(
                    index = index,
                    count = options.size,
                    isHighlight = value in existingSummaries,
                    icon = {
                        SegmentedButtonDefaults.Icon(isCurrentSelected, activeContent = {
                            Icon(
                                imageVector = ArxivIcons.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                            )
                        })
                    },
                    selected = isCurrentSelected,
                    text = value.toButtonText()
                ) {
                    if (isStateIdle) updateSummaryType(value)
                }
            }
        }
        Text(text = selectedEntry?.toDescText().orEmpty(), style = HelperUnderTextTheme)
    }

}

@Composable
private fun CancelButton(
    modifier: Modifier = Modifier, onCancelClick: () -> Unit
) {
    SecondaryButton(
        modifier = modifier,
        onClick = onCancelClick,
    ) {
        Text(text = "Cancel", color = MaterialTheme.colorScheme.onSecondary)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = ArxivIcons.Close,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SummaryText(summaryState: SummaryUiState) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val isIdleSummary = summaryState.isIdle()
        val summaryText = if (isIdleSummary) "Summary" else "Summarizing"
        Text(text = summaryText, style = PrimaryHeaderText)

        if (!isIdleSummary) {
            CircularProgressIndicator(
                strokeCap = StrokeCap.Round, strokeWidth = 2.dp, modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun BoxScope.ScrollDownButtonWithAnimation(
    scrollState: ScrollState,
    scrollUiState: SummaryScrollUiState, onClick: () -> Unit
) {
//    val isNotScrolledToBottom by remember(scrollState) {
//        derivedStateOf {
//            val layoutInfo = scrollState.layoutInfo
//            val lastItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
//            val totalItemsCount = layoutInfo.totalItemsCount
//            lastItemIndex < totalItemsCount - 1
//        }
//    }

    var maxValue = scrollState.maxValue
    if (maxValue == Int.MAX_VALUE)
        maxValue = 0

    val isNotScrolledToBottom = scrollState.value < maxValue

    AnimatedVisibility(
        modifier = Modifier.align(Alignment.BottomCenter),
        visible = isNotScrolledToBottom && scrollUiState is SummaryScrollUiState.Idle,
        enter = fadeIn(animationSpec = tween(durationMillis = 250)),
        exit = fadeOut(animationSpec = tween(durationMillis = 250))
    ) {
        ScrollDownButton(onClick = onClick)
    }
}

@Composable
private fun ScrollDownButton(
    onClick: () -> Unit
) {
    val padding = 12.dp
    Card(
        modifier = Modifier
            .size(64.dp + padding * 2, 40.dp + padding * 2)
            .padding(padding),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
//        elevation = CardDefaults.cardElevation(
//            defaultElevation = 2.dp
//        ),
        onClick = onClick,
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                ArxivIcons.DoubleArrow,
                contentDescription = "Scroll to bottom",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(22.dp)
            )
        }

    }
}

@DefaultPreview
@Composable
private fun SummaryPreview() {
    ArxivTheme {
        Box {
            ScrollDownButton {}
        }
    }
}