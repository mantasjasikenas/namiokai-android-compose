package com.github.mantasjasikenas.core.ui.component

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.core.common.util.Constants.EMAIL_ADDRESS
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.rememberState

@Composable
fun ReportBugDialog(
    dialogState: Boolean,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    AnimatedVisibility(visible = dialogState) {
        ReportBugDialog(
            onDismiss = onDismiss,
            onSaveClick = { type, name, description ->
                val intent = Intent(Intent.ACTION_SEND)

                intent.type = "text/plain"
                intent.putExtra(
                    Intent.EXTRA_EMAIL,
                    arrayOf(EMAIL_ADDRESS)
                )
                intent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "[Namiokai] $type: $name"
                )
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    description
                )
                intent.setType("message/rfc822")

                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportBugDialog(
    onDismiss: () -> Unit,
    onSaveClick: (type: String, name: String, description: String) -> Unit
) {
    val context = LocalContext.current
    var expanded by rememberState {
        false
    }
    var bugTopic by rememberState {
        ""
    }
    var bugDetails by rememberState {
        ""
    }
    val items = listOf(
        "Bug Report",
        "Feature Request",
        "Other",
    )
    var selectedType by remember { mutableStateOf("Type") }

    NamiokaiDialog(
        title = "New issue",
        onSaveClick = {
            if (bugTopic.isEmpty() || bugDetails.isEmpty() || !items.contains(selectedType)) {
                Toast.makeText(
                    context,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@NamiokaiDialog
            }

            onSaveClick(
                selectedType,
                bugTopic,
                bugDetails
            )
        },
        onDismiss = onDismiss
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    items.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                selectedType = item
                                expanded = false
                            }
                        )
                    }
                }
            }

            NamiokaiSpacer(height = 16)
            NamiokaiTextField(
                modifier = Modifier,
                label = "Title",
                onValueChange = { bugTopic = it },
            )
            NamiokaiSpacer(height = 16)
            NamiokaiTextArea(
                modifier = Modifier.height(150.dp),
                label = "Detailed description",
                onValueChange = { bugDetails = it },
            )
        }

    }

}