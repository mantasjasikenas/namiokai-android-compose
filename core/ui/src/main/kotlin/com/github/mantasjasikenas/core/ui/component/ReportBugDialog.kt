package com.github.mantasjasikenas.core.ui.component

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Workspaces
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.core.common.util.Constants.EMAIL_ADDRESS
import com.github.mantasjasikenas.core.ui.R
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

@Composable
fun ReportBugDialog(
    onDismiss: () -> Unit,
    onSaveClick: (type: String, name: String, description: String) -> Unit
) {
    val context = LocalContext.current

    var bugTopic by rememberState { "" }
    var bugDetails by rememberState { "" }

    val items = listOf(
        stringResource(R.string.bug_report),
        stringResource(R.string.feature_request),
        stringResource(R.string.other),
    )
    var selectedType by remember { mutableStateOf("") }

    NamiokaiDialog(
        title = "Report issue",
        onSaveClick = {
            if (bugTopic.isEmpty() || bugDetails.isEmpty() || !items.contains(selectedType)) {
                Toast.makeText(
                    context,
                    context.getString(R.string.please_fill_in_all_fields),
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
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.issue_type),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 7.dp)
            )

            NamiokaiDropdownMenu(
                label = stringResource(R.string.issue_type),
                items = items,
                onItemSelected = {
                    selectedType = it
                },
                leadingIconVector = Icons.Outlined.Workspaces,
                itemLabel = { it },
            )

            NamiokaiSpacer(height = 16)

            Text(
                text = stringResource(R.string.type),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 7.dp)
            )
            NamiokaiTextField(
                modifier = Modifier,
                label = stringResource(R.string.type),
                onValueChange = { bugTopic = it },
            )

            NamiokaiSpacer(height = 16)

            Text(
                text = stringResource(R.string.description),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
            )
            NamiokaiTextArea(
                modifier = Modifier.height(150.dp),
                label = stringResource(R.string.detailed_description),
                onValueChange = { bugDetails = it },
            )
        }

    }

}