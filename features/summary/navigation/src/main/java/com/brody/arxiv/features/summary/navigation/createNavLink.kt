package com.brody.arxiv.features.summary.navigation

import android.net.Uri
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import kotlinx.serialization.json.Json

fun createNavLink(
    paperModel: SaveablePaperDataModel,
    isDeepLink: Boolean = false
): String {
    val paperModelJson = Json.encodeToString(SaveablePaperDataModel.serializer(), paperModel)
    val encodedPaperModelJson = Uri.encode(paperModelJson)

    return "$SUMMARY_ROUTE/$encodedPaperModelJson/${isDeepLink}"
}