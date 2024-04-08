package com.brody.arxiv.features.summary.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MarkdownTextRenderer(markdownText: String, modifier: Modifier = Modifier) {
    val sections = markdownText.split("\n")
    Column(modifier = modifier.padding(8.dp)) {
        sections.forEach { section ->
            when {
                section.startsWith("**") && section.endsWith("**") -> {
                    // Header
                    val text = section.removePrefix("**").removeSuffix("**")
                    Text(
                        text = text,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                section.trim().startsWith("-") -> {
                    // Unordered List
                    val items = section.split("\n-").map { it.trim() }
                    items.forEach { item ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(
                                text = "• $item",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                section.trim().matches("""^\d+\..*""".toRegex()) -> {
                    // Ordered List
                    val items = section.split("\n").map { it.trim() }
                    items.forEachIndexed { index, item ->
                        val (number, text) = item.split(".", limit = 2)
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(
                                text = "$number. $text",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                else -> {
                    // Regular Text
                    Text(
                        text = section,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

//    Column(
//        modifier = textModifier
//    ) {
//        sections.forEach { section ->
//            // Check if the section contains embedded lists denoted by "- "
//            if (section.contains("\n-")) {
//                val parts = section.split("\n-")
//                parts.forEachIndexed { index, part ->
//                    if (index == 0) {
//                        // The first part is text before the first list item or the entire section if no list present
//                        if (part.trim().startsWith("-")) {
//                            RenderBulletListItem(part.trim().drop(1), paddingValues)
//                        } else {
//                            RenderText(part, paddingValues)
//                        }
//                    } else {
//                        // Render list items
//                        RenderBulletListItem(part, paddingValues)
//                    }
//                }
//            } else {
//                // Render regular paragraph
//                RenderText(section, paddingValues)
//            }
//        }
//    }