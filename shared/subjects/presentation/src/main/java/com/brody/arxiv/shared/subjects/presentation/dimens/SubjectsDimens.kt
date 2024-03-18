package com.brody.arxiv.shared.subjects.presentation.dimens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brody.arxiv.designsystem.dimens.Dimens

internal object SubjectsDimens {
    val flowRowPadding = Dimens.spacingNormal
    val horizontalSpacing = Dimens.spacingTiny
    val verticalSpacing = Dimens.zeroDp

    val chipHorizontalPadding = Dimens.spacingTinyHalf
    val chipVerticalPadding = Dimens.spacingTiny

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