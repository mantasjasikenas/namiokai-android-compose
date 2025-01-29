package com.github.mantasjasikenas.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.mantasjasikenas.core.ui.R

@Composable
fun NamiokaiConfirmDialog(
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    AlertDialog(onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.are_you_sure))
        },
        text = {
            Text(text = stringResource(R.string.this_action_cannot_be_undone))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        })
}

@Composable
fun NamiokaiDialog(
    title: String,
    onSaveClick: () -> Unit,
    onDismiss: () -> Unit,
    buttonsVisible: Boolean = true,
    content: @Composable () -> Unit
) {
    NamiokaiDialog(
        title = title,
        buttonsVisible = buttonsVisible,
        selectedValue = null,
        onSaveClick = { onSaveClick() },
        onDismiss = onDismiss,
    ) {
        content()
    }
}

@Composable
fun <T> NamiokaiDialog(
    title: String,
    buttonsVisible: Boolean = true,
    selectedValue: T,
    onSaveClick: (T) -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(30.dp))
                content()
                Spacer(modifier = Modifier.height(30.dp))

                if (buttonsVisible) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = onDismiss
                        ) {
                            Text(text = stringResource(R.string.cancel))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        TextButton(onClick = { onSaveClick(selectedValue) }) {
                            Text(text = stringResource(R.string.ok))
                        }
                    }
                }
            }
        }
    }
}