package com.brody.arxiv.features.details.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.HorizontalDivider
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
import com.brody.arxiv.designsystem.annotations.DefaultPreview
import com.brody.arxiv.designsystem.theme.ArxivTheme
import com.brody.arxiv.designsystem.ui.appbar.ArxivTopAppBar
import com.brody.arxiv.designsystem.ui.button.PrimaryButton
import com.brody.arxiv.designsystem.ui.button.SecondaryButton
import com.brody.arxiv.designsystem.ui.components.ArxivNavigationBar
import com.brody.arxiv.designsystem.ui.icons.ArxivIcons
import com.brody.arxiv.features.details.presentation.components.DetailsAuthorsList
import com.brody.arxiv.features.details.presentation.models.DetailsUiCategory
import com.brody.arxiv.features.details.presentation.models.DetailsUiModel
import com.brody.arxiv.features.details.presentation.utils.openUrlInCustomTab
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun DetailsScreen(
    detailsUiModel: DetailsUiModel,
    onBackClicked: () -> Unit
) {
    DetailsScreenInternal(
        detailsUiModel = detailsUiModel,
        onBackClicked = onBackClicked
    )
}

@Composable
private fun DetailsScreenInternal(
    detailsUiModel: DetailsUiModel,
    onBackClicked: () -> Unit,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.connectDetailsItem(detailsUiModel)
    }

    val scrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(scrollState)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    (uiState as? DetailsUiState.Connected)?.let {
        val model = it.model

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                // Add scroll behaviour to top bar
                val currentContext = LocalContext.current
                ArxivTopAppBar(
                    navigationIcon = ArxivIcons.ArrowBack,
                    onNavigationClick = onBackClicked,
                    actionIcons = if (model.isConsistent) listOf(
                        ArxivIcons.OpenInBrowser,
                        if (model.isSaved)
                            ArxivIcons.SaveActive else ArxivIcons.SaveInactive
                    ) else null,
                    onActionClicks = listOf(
                        {
                            openUrlInCustomTab(currentContext, model.id)
                        },
                        {
                            viewModel.saveItem(model.id, model)
                        }
                    ),
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = { BottomBar() }
        ) { padding ->
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
            showToast(currentContext, category)
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
            text = model.summary,
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 16.sp
            )
        )
        Spacer(Modifier.height(16.dp))
    }
//
//    Spacer(Modifier.height(16.dp))

}

private fun showToast(context: Context, category: DetailsUiCategory) {
    Toast.makeText(
        context,
        "${category.name} clicked, functionality for opening it will be added soon",
        Toast.LENGTH_SHORT
    ).show()
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
private fun CategoriesList(
    categories: List<DetailsUiCategory>,
    onClick: (DetailsUiCategory) -> Unit
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
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(22.dp)
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
private fun BottomBar() {
    ArxivNavigationBar(isCustom = true) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp)
        ) {
            val isEnabled = false
            SecondaryButton(
                enabled = isEnabled,
                onClick = {},
                modifier = Modifier.weight(0.5f)
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
            PrimaryButton(
                onClick = {},
                modifier = Modifier.weight(0.5f)
            ) {
                Icon(
                    imageVector = ArxivIcons.Download,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Download", style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}



@Preview()
@Composable
private fun DetailsPreview() {
    ArxivTheme {
        BottomBar()
//        DatesRow("5 Feb 2024", "4 Feb 2024")
//        CategoriesList(listOf(DetailsUiCategory("Astronomy", ""))) {}
    }
}
