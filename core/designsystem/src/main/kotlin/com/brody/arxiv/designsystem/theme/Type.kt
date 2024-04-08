package com.brody.arxiv.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    // ONBOARDING small
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp,
        color = OnSurface
    ),
    // #OnBoarding under Search text
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        color = OnSurfaceVariant
    ),

    // Paper item text
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.25.sp,
        color = OnSurfaceVariant
    ),

    //Toolbar Title
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        color = OnSurface
    ),

    //TITLES IN DIALOGS
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = .15.sp,
        color = OnSurface
    ),

    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = OnSurfaceVariant
    )
)

val OnboardingTitleLargeTheme = Typography.titleLarge.copy(
    fontWeight = FontWeight.Medium,
    fontSize = 22.sp,
    color = OnPrimaryContainer
)

val OnboardingSmallTheme = Typography.bodyLarge.copy(
    lineHeight = 24.sp,
    color = OnSurface60
)

val HelperUnderTextTheme = Typography.bodyMedium.copy(
    color = OnSurface60,
    letterSpacing = (0.1).sp
)

val OnboardingUnderSearchTheme = HelperUnderTextTheme.copy(
    fontWeight = FontWeight.Medium,
)

val PrimaryHeaderText = Typography.bodyMedium.copy(
    fontWeight = FontWeight.Medium,
    color = Primary
)

val SecondaryHeaderText = Typography.bodyMedium.copy(
    fontWeight = FontWeight.Medium,
    color = OnSecondary
)

val FilterTitleTextTheme = Typography.bodyLarge.copy(
    fontWeight = FontWeight.Medium,
    color = OnSurface40,
)

val PaperLabelSmall = Typography.labelSmall.copy(
    letterSpacing = 0.1.sp
)