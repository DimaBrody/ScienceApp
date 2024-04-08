package com.brody.arxiv.features.details.presentation

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brody.arxiv.core.common.extensions.toInt
import com.brody.arxiv.core.common.ui.showToast
import com.brody.arxiv.core.pdf.download.models.PdfLink
import com.brody.arxiv.designsystem.theme.ArxivTheme
import com.brody.arxiv.designsystem.ui.appbar.ArxivTopAppBar
import com.brody.arxiv.designsystem.ui.buttons.PrimaryButton
import com.brody.arxiv.designsystem.ui.buttons.SecondaryButton
import com.brody.arxiv.designsystem.ui.components.ArxivNavigationBar
import com.brody.arxiv.designsystem.ui.icons.ArxivIcons
import com.brody.arxiv.features.details.presentation.components.DetailsAuthorsList
import com.brody.arxiv.features.details.presentation.models.DetailsUiCategory
import com.brody.arxiv.features.details.presentation.models.DetailsUiModel
import com.brody.arxiv.features.details.presentation.models.toSaveableModel
import com.brody.arxiv.features.details.presentation.utils.openUrlInCustomTab
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DetailsScreen(
    saveableDataModel: SaveablePaperDataModel,
    onSummaryClicked: (SaveablePaperDataModel) -> Unit,
    onBackClicked: () -> Unit
) {
    DetailsScreenInternal(
        saveableDataModel = saveableDataModel,
        onSummaryClicked = onSummaryClicked,
        onBackClicked = onBackClicked
    )
}

@Composable
private fun DetailsScreenInternal(
    saveableDataModel: SaveablePaperDataModel,
    onSummaryClicked: (SaveablePaperDataModel) -> Unit,
    onBackClicked: () -> Unit,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.connectDetailsItem(saveableDataModel)
    }

    val scrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(scrollState)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    (uiState as? DetailsUiState.Connected)?.let { connectedState ->
        val model = connectedState.model
        val currentContext = LocalContext.current

        Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
            // Add scroll behaviour to top bar
            ArxivTopAppBar(
                navigationIcon = ArxivIcons.ArrowBack,
                onNavigationClick = onBackClicked,
                actionIcons = if (model.isConsistent) listOf(
                    ArxivIcons.OpenInBrowser,
                    if (model.isSaved) ArxivIcons.SaveActive else ArxivIcons.SaveInactive
                ) else null,
                onActionClicks = listOf({
                    openUrlInCustomTab(currentContext, model.id)
                }, {
                    viewModel.saveItem(model.id, model)
                }),
                scrollBehavior = scrollBehavior
            )
        }, bottomBar = {
            BottomBar(model = model,
                onDownloadStart = viewModel::downloadPdf,
                onDownloadState = viewModel.onDownloadState,
                onSummaryClicked = onSummaryClicked,
                onOpenPdf = { viewModel.openPdfByName(currentContext, it) })
        }) { padding ->
            val innerScrollState = rememberScrollState()
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .verticalScroll(innerScrollState),
            ) {
                if (model.isConsistent) {
                    PaperDetails(model)
                } else {
                    Box {
                        Text(
                            text = "Something wrong with item",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

private val HorizontalPaddingValues = PaddingValues(horizontal = 20.dp)

@Composable
private fun PaperDetails(model: DetailsUiModel) {
    Column(Modifier.padding(HorizontalPaddingValues)) {
        Text(text = model.title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        val currentContext = LocalContext.current
        DetailsAuthorsList(model.authors) { url ->
            openUrlInCustomTab(currentContext, url)
        }

        Spacer(Modifier.height(16.dp))
        CategoriesList(model.categories) { category ->
            showCategoryToast(currentContext, category)
        }

        DatesRow(model.updated, model.published)
    }


//    HorizontalDivider(
//        color = MaterialTheme.colorScheme.outlineVariant,
//        modifier = Modifier.padding(vertical = 16.dp)
//    )
    Spacer(Modifier.height(16.dp))

    Column(Modifier.padding(HorizontalPaddingValues)) {
        Text(
            text = "Abstract", style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ), color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = model.summary, style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 16.sp
            )
        )
        Spacer(Modifier.height(16.dp))
    }
//
//    Spacer(Modifier.height(16.dp))

}

private fun showCategoryToast(context: Context, category: DetailsUiCategory) {
    showToast(
        context, "${category.name} clicked, functionality for opening it will be added soon"
    )
}


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
private fun CategoriesList(
    categories: List<DetailsUiCategory>, onClick: (DetailsUiCategory) -> Unit
) {
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        categories.forEach { category ->
            Chip(
                onClick = { onClick(category) }, colors = ChipDefaults.outlinedChipColors(
                    backgroundColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ), border = BorderStroke(
                    width = 1.dp, color = MaterialTheme.colorScheme.onSurfaceVariant
                ), shape = RoundedCornerShape(8.dp), modifier = Modifier.height(22.dp)
            ) {
                Text(
                    category.name,
                    style = MaterialTheme.typography.labelSmall.copy(),
                )
            }
        }
    }
}

@Composable
private fun DatesRow(updated: String, published: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Text(
            text = "Updated at $updated",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.1.sp)
        )
        Box(Modifier.padding(bottom = 0.5.dp)) {
            Spacer(
                Modifier
                    .size(1.dp, 10.dp)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
        Text(
            text = "Published at $published",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.1.sp),
        )
    }
}

@Composable
private fun BottomBar(
    model: DetailsUiModel,
    onDownloadStart: (String, String) -> Unit,
    onDownloadState: () -> StateFlow<DownloadUiState>,
    onSummaryClicked: (SaveablePaperDataModel) -> Unit,
    onOpenPdf: (String) -> Unit
) {
    val downloadUiState by onDownloadState().collectAsStateWithLifecycle()

    ArxivNavigationBar(isCustom = true) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp)
        ) {
            val isEnabled = downloadUiState is DownloadUiState.ReadyToRead
            SecondaryButton(
                enabled = isEnabled,
                onClick = {
                    if (isEnabled) onSummaryClicked(model.toSaveableModel())
                },
                modifier = Modifier
                    .weight(0.5f)
                    .alpha(0.7f + isEnabled.toInt() * 0.3f),
            ) {
                Icon(
                    imageVector = ArxivIcons.Summary,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Summary", style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = if (isEnabled) MaterialTheme.colorScheme.onSecondary else Color(
                            0xFF999392
                        )
                    )
                )
            }
            Spacer(Modifier.width(20.dp))

            DownloadButton(model, onDownloadStart, downloadUiState, onOpenPdf)
        }
    }
}

@Composable
private fun RowScope.DownloadButton(
    model: DetailsUiModel,
    onDownloadStart: (String, String) -> Unit,
    downloadUiState: DownloadUiState,
    openPdf: (String) -> Unit
) {
    val isModelHasPdfLink = model.pdfLink != null
    val pdfLinkNotNull = model.pdfLink.orEmpty()


    PrimaryButton(
        onClick = {
            when (downloadUiState) {
                is DownloadUiState.Ignore, is DownloadUiState.ReadyToLoad, is DownloadUiState.Failed -> onDownloadStart(
                    pdfLinkNotNull,
                    model.pdfName
                )

                is DownloadUiState.ReadyToRead -> openPdf(downloadUiState.fileName)

                else -> {}
            }
        },
        enabled = isModelHasPdfLink,
        modifier = Modifier
            .weight(0.5f)
            .alpha(0.38f + 0.62f * (downloadUiState !is DownloadUiState.Downloading).toInt())
    ) {
        if (isModelHasPdfLink) {
            when (downloadUiState) {
                is DownloadUiState.Ignore, is DownloadUiState.ReadyToLoad, is DownloadUiState.Failed -> {
                    if (downloadUiState is DownloadUiState.Failed) {
                        showToast(
                            LocalContext.current,
                            downloadUiState.message
                        )
                    }

                    DownloadButtonContent(text = "Download") {
                        Icon(
                            imageVector = ArxivIcons.Download,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                is DownloadUiState.ReadyToRead -> {
                    DownloadButtonContent(text = "Read") {
                        Icon(
                            imageVector = ArxivIcons.Pdf,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }

                is DownloadUiState.Downloading -> {
                    DownloadButtonContent(text = "Loading") {
                        CircularProgressIndicator(
                            progress = { downloadUiState.progress },
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        } else {
            Text(
                text = "No PDF", style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium, color = Color(0xFF999392)
                )
            )
        }

    }
}

@Composable
private fun DownloadButtonContent(
    text: String, onLeadingContent: @Composable () -> Unit
) {
    Box(modifier = Modifier.size(18.dp)) {
        onLeadingContent.invoke()
    }
    Spacer(Modifier.width(8.dp))
    Text(
        text = text, style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Preview()
@Composable
private fun DetailsPreview() {
    ArxivTheme {
        Row {
            DownloadButton(model = DetailsUiModel(
                id = "ea",
                updated = "consectetur",
                published = "nibh",
                title = "tincidunt",
                summary = "percipit",
                authors = persistentListOf(),
                doi = "deseruisse",
                links = persistentListOf(PdfLink(true, "PDF")),
                comment = "interdum",
                categories = persistentListOf(),
                isConsistent = false,
                isSaved = false,
                hasSummaries = false
            ),
                onDownloadStart = { s: String, s1: String -> },
                downloadUiState = DownloadUiState.Ignore,
                openPdf = {})
        }
//        BottomBar(mo)
//        DatesRow("5 Feb 2024", "4 Feb 2024")
//        CategoriesList(listOf(DetailsUiCategory("Astronomy", ""))) {}
    }
}
