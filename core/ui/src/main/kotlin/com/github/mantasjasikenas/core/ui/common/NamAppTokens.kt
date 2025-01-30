package com.github.mantasjasikenas.core.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp

object NamiokaiUiTokens {
    private val pageContentPadding = 16.dp
    private val FabButtonContainerHeight = 120.dp

    val ItemSpacing = 16.dp
    val PageContentPadding = PaddingValues(pageContentPadding)
    val PageContentPaddingWithFab =
        PaddingValues(
            start = pageContentPadding,
            end = pageContentPadding,
            top = pageContentPadding,
            bottom = FabButtonContainerHeight
        )
}
