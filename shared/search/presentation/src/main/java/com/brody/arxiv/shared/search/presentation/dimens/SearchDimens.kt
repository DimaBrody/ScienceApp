package com.brody.arxiv.shared.search.presentation.dimens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brody.arxiv.designsystem.dimens.Dimens
import com.brody.arxiv.designsystem.dimens.Dimens.spacingNormal
import com.brody.arxiv.designsystem.dimens.Dimens.zeroDp

internal object SearchDimens {
    val InactiveSearchPadding = zeroDp
    val ActiveSearchPadding = spacingNormal

    val searchBarVerticalPadding: Dp = Dimens.spacingSmall
    val searchTitleTextPaddingEnd = Dimens.spacingNormalSpecial

    // Bottom Sheet
    val bsSmallText = Dimens.spacingTinyHalf
    private val bsContentPadding = Dimens.spacingBig
    val bsContentPaddings = PaddingValues(
        start = bsContentPadding,
        end = bsContentPadding,
        bottom = bsContentPadding
    )

    // List Item
    val countTextSize = 16.sp


}