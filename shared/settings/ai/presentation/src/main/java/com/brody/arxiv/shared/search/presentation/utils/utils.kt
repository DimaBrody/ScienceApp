package com.brody.arxiv.shared.search.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.brody.arxiv.shared.settings.ai.models.presentation.LanguageUiModel
import com.brody.arxiv.shared.settings.ai.presentation.R

@Composable
fun LanguageUiModel.getPainterResource(): Painter = painterResource(
    when (this) {
        LanguageUiModel.GEMINI -> R.drawable.ic_gemini
        LanguageUiModel.OPENAI -> R.drawable.ic_openai
        LanguageUiModel.CUSTOM -> R.drawable.ic_custom
    }
)