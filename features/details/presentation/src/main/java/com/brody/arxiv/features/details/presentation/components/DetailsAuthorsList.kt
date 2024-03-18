package com.brody.arxiv.features.details.presentation.components

import android.net.Uri
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.brody.arxiv.designsystem.theme.OnSurface60

@Composable
internal fun DetailsAuthorsList(authors: List<String>, navigateToUrl: (String) -> Unit) {
    // A custom wrap layout for author names
    FlowLayout {
        authors.forEachIndexed { index, author ->
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(author)
                }
            }
            ClickableText(
                text = annotatedString,
                onClick = {
                    navigateToUrl("https://arxiv.org/search/?query=${Uri.encode(author)}&searchtype=author")
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    letterSpacing = 0.sp,
                    color = OnSurface60
                )
            )
            // Add a comma and space except for the last item
            if (index < authors.size - 1) {
                Text(", ")
            }
        }
    }
}

@Composable
private fun FlowLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val rows = mutableListOf<RowMeasurements>()
        var currentRow = RowMeasurements()
        measurables.forEach { measurable ->
            val placeable = measurable.measure(constraints)

            if (currentRow.width + placeable.width > constraints.maxWidth) {
                rows.add(currentRow)
                currentRow = RowMeasurements()
            }

            currentRow.placeables.add(placeable)
            currentRow.width += placeable.width
        }

        rows.add(currentRow) // Add the last row

        val height = rows.sumOf { it.maxHeight }
        layout(constraints.maxWidth, height) {
            var yPosition = 0
            rows.forEach { row ->
                var xPosition = 0
                row.placeables.forEach { placeable ->
                    placeable.placeRelative(x = xPosition, y = yPosition)
                    xPosition += placeable.width
                }
                yPosition += row.maxHeight
            }
        }
    }
}

private data class RowMeasurements(
    val placeables: MutableList<Placeable> = mutableListOf(),
    var width: Int = 0
) {
    val maxHeight get() = placeables.maxOfOrNull { it.height } ?: 0
}
