package com.brody.arxiv.designsystem.ui.buttons

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import com.brody.arxiv.designsystem.theme.Primary

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.secondary,
//    disabledContainerColor: Color = FigmaColors.primaryLight70(),
//    disabledContentColor: Color = FigmaColors.primaryOriginal(),
    text: String? = null, enabled: Boolean = true,
    onClick: () -> Unit,
    content: (@Composable RowScope.() -> Unit)? = null,
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,

        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
    ) {
        content?.invoke(this) ?: text?.let {
            Text(
                text = it, color = textColor
            )
        }
    }
}

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
    textColor: Color = MaterialTheme.colorScheme.onSecondary,
//    disabledContainerColor: Color = FigmaColors.primaryLight70(),
//    disabledContentColor: Color = FigmaColors.primaryOriginal(),
    text: String? = null, enabled: Boolean = true, onClick: () -> Unit,
    content: (@Composable RowScope.() -> Unit)? = null,
) {
    PrimaryButton(
        modifier, backgroundColor, textColor, text, enabled, onClick, content
    )
}

@Composable
fun DefaultTextButton(
    modifier: Modifier = Modifier,
    textColor: Color = Primary,
//    disabledContainerColor: Color = FigmaColors.primaryLight70(),
//    disabledContentColor: Color = FigmaColors.primaryOriginal(),
    textId: Int,
    enabled: Boolean = true,
    textDecoration: TextDecoration? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    onClick: () -> Unit

) {
    TextButton(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
    ) {
        Text(
            text = stringResource(id = textId),
            color = textColor,
            textDecoration = textDecoration,
            style = textStyle
        )
    }
}