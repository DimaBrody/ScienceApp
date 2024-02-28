package com.brody.arxiv.onboarding.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.brody.arxiv.designsystem.R
import com.brody.arxiv.designsystem.dimens.Dimens
import com.brody.arxiv.designsystem.theme.OnboardingTitleLarge
import com.brody.arxiv.designsystem.ui.button.PrimaryButton

@Composable
fun LandingPage(onNextClicked: () -> Unit) {
    val logoResource = painterResource(id = R.drawable.arxiv_logo_vec)

    val titleText = stringResource(com.brody.arxiv.onboarding.R.string.welcome_title)
    val subtitleText = stringResource(com.brody.arxiv.onboarding.R.string.welcome_subtitle)
    val nextText = stringResource(com.brody.arxiv.onboarding.R.string.next)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(Dimens.spacingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = logoResource,
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(Dimens.spacingExtraLarge))
            Text(
                titleText,
                style = OnboardingTitleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dimens.spacingBig))
            Text(
                subtitleText,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dimens.spacingExtraLarge))
        }
        PrimaryButton(
            onClick = onNextClicked,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            text = nextText
        )
    }
}