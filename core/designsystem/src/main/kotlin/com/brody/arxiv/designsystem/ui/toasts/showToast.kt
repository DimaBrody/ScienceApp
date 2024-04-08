package com.brody.arxiv.designsystem.ui.toasts

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.brody.arxiv.core.common.ui.showToast

@SuppressLint("ComposableNaming")
@Composable
fun showComposeToast(message: String) = showToast(LocalContext.current, message)