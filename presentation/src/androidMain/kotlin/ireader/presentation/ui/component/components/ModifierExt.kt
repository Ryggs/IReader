package ireader.presentation.ui.component.components

import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.ui.Modifier


actual fun Modifier.statusBarsPadding(): Modifier = this.safeContentPadding()