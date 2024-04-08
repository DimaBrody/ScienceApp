package com.brody.arxiv.designsystem.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.automirrored.outlined.TextSnippet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowDown
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material.icons.outlined.TextSnippet

object ArxivIcons {
    val ArrowBack = Icons.AutoMirrored.Default.ArrowBack
    val SmallArrowRight = Icons.AutoMirrored.Default.KeyboardArrowRight
    val Close = Icons.Filled.Close
    val Check = Icons.Filled.Check
    val Search = Icons.Filled.Search
    val Info = Icons.Outlined.Info
    val OpenInBrowser = Icons.AutoMirrored.Outlined.OpenInNew
    val DoubleArrow = Icons.Outlined.KeyboardDoubleArrowDown

    val Summary = Icons.AutoMirrored.Outlined.TextSnippet
    val Pdf = Icons.Outlined.PictureAsPdf
    val Download = Icons.Filled.Download
    val SummaryAlt = Icons.Outlined.Summarize

    // Bottom Navigation
    val PapersActive = Icons.AutoMirrored.Filled.Article
    val PapersInactive = Icons.AutoMirrored.Outlined.Article
    val Filter = Icons.Filled.FilterList

    val Explore = Icons.Outlined.Search
    val Chart = Icons.Filled.BarChart

    val SavedActive = Icons.Filled.Bookmarks
    val SavedInactive = Icons.Outlined.Bookmarks
    val SavedHistory = Icons.Filled.History

    val SaveActive = Icons.Filled.Bookmark
    val SaveInactive = Icons.Outlined.BookmarkBorder

    val SettingsActive = Icons.Filled.Settings
    val SettingsInactive = Icons.Outlined.Settings

    // Password
    val PasswordVisible = Icons.Filled.VisibilityOff
    val PasswordHidden = Icons.Filled.Visibility

}