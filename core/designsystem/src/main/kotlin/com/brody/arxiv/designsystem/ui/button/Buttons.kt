package com.brody.arxiv.designsystem.ui.button

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.brody.arxiv.designsystem.theme.OnPrimary
import com.brody.arxiv.designsystem.theme.Primary

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Primary,
    textColor: Color = OnPrimary,
//    disabledContainerColor: Color = FigmaColors.primaryLight70(),
//    disabledContentColor: Color = FigmaColors.primaryOriginal(),
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        )
    ){
        Text(
            text = text,
            color = textColor
        )
    }
}

@Composable
fun DefaultTextButton(
    modifier: Modifier = Modifier,
    textColor: Color = Primary,
//    disabledContainerColor: Color = FigmaColors.primaryLight70(),
//    disabledContentColor: Color = FigmaColors.primaryOriginal(),
    textId: Int,
    enabled: Boolean = true,
    onClick: () -> Unit

) {
    TextButton(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
    ){
        Text(
            text = stringResource(id = textId),
            color = textColor
        )
    }
}