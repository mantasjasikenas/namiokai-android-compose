package com.github.mantasjasikenas.core.ui.common.bill

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.mantasjasikenas.core.ui.R
import com.github.mantasjasikenas.core.ui.common.NamiokaiBottomSheet
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.component.NamiokaiConfirmDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDetailsBottomSheetWrapper(
    title: String,
    isAllowedModification: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    var confirmDialog by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    NamiokaiBottomSheet(
        title = title,
        onDismiss = onDismiss,
        bottomSheetState = bottomSheetState
    ) {
        content()

        NamiokaiSpacer(height = 30)

        AnimatedVisibility(visible = isAllowedModification) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {

                TextButton(onClick = {
                    onEdit()
                    onDismiss()
                }) {
                    Text(text = stringResource(R.string.edit))
                }
                TextButton(onClick = {
                    confirmDialog = true
                }) {
                    Text(text = stringResource(R.string.delete))
                }
            }
            NamiokaiSpacer(height = 30)

            if (confirmDialog) {
                NamiokaiConfirmDialog(
                    onConfirm = {
                        scope.launch { bottomSheetState.hide() }
                            .invokeOnCompletion {
                                if (!bottomSheetState.isVisible) {
                                    onDismiss()
                                }
                            }
                        onDelete()
                        confirmDialog = false
                    },
                    onDismiss = { confirmDialog = false })
            }
        }
    }
}