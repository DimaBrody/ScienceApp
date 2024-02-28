package com.brody.arxiv.shared.search.presentation.ui

import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import com.brody.arxiv.designsystem.theme.OnSurface
import com.brody.arxiv.designsystem.theme.OnSurface60
import com.brody.arxiv.designsystem.theme.OnSurfaceVariant
import com.brody.arxiv.designsystem.theme.Outline
import com.brody.arxiv.designsystem.theme.SecondarySurface

@Composable
fun getSearchBarColors() = SearchBarDefaults.colors(
    containerColor = SecondarySurface,
    dividerColor = Outline,
    inputFieldColors = SearchBarDefaults.inputFieldColors(
        unfocusedPlaceholderColor = OnSurface60,
        focusedPlaceholderColor = OnSurface60,
        focusedTextColor = OnSurface,
        unfocusedTextColor = OnSurface,
        focusedLeadingIconColor = OnSurfaceVariant,
        unfocusedLeadingIconColor = OnSurfaceVariant,
        focusedTrailingIconColor = OnSurfaceVariant,
        unfocusedTrailingIconColor = OnSurfaceVariant
    )
)