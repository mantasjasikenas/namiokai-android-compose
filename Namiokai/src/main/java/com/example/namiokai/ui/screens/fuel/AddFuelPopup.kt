package com.example.namiokai.ui.screens.fuel

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.namiokai.R
import com.example.namiokai.model.Destination
import com.example.namiokai.model.Fuel
import com.example.namiokai.model.isValid
import com.example.namiokai.model.toUidAndDisplayNamePair
import com.example.namiokai.ui.main.UsersMap
import com.example.namiokai.ui.common.NamiokaiDialog
import com.example.namiokai.ui.common.UsersPicker

@Composable
fun AddFuelPopup(
    onSaveClick: (Fuel) -> Unit,
    onDismiss: () -> Unit,
    users: UsersMap,
    destinations: List<Destination>
) {
    val fuel by remember {
        mutableStateOf(Fuel())
    }
    val splitFuelHashMap = remember {
        users.map { it.value.toUidAndDisplayNamePair() to false }.toMutableStateMap()
    }
    val driverSelectHashMap = remember {
        users.map { it.value.toUidAndDisplayNamePair() to false }.toMutableStateMap()
    }

    val (selectedOption, onOptionSelected) = remember { mutableStateOf(destinations[0]) }
    val context = LocalContext.current

    NamiokaiDialog(
        title = "Select trip details",
        selectedValue = fuel,
        onSaveClick = {
            fuel.driverUid =
                driverSelectHashMap.filter { it.value }.keys.map { it.first }
                    .firstOrNull()
                    ?: ""
            fuel.passengersUid =
                splitFuelHashMap.filter { it.value }.keys.map { it.first }
            fuel.tripDestination = selectedOption.name
            fuel.tripPricePerUser = when (fuel.passengersUid.count()) {
                1 -> selectedOption.tripPriceAlone
                else -> selectedOption.tripPriceWithOthers
            }

            if (!fuel.isValid()) {
                Toast.makeText(
                    context,
                    R.string.please_fill_all_fields,
                    Toast.LENGTH_SHORT
                ).show()
                return@NamiokaiDialog
            }

            onDismiss()
            onSaveClick(fuel)
            Toast.makeText(context, R.string.fuel_saved, Toast.LENGTH_SHORT).show()
        },
        onDismiss = onDismiss
    )
    {
        Text(
            text = stringResource(R.string.driver),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        UsersPicker(usersPickup = driverSelectHashMap, isMultipleSelectEnabled = false)
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = stringResource(R.string.passengers),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        UsersPicker(usersPickup = splitFuelHashMap, isMultipleSelectEnabled = true)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.destination),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 5.dp)
        )

        Column()
        {
            destinations.forEach { dest ->
                Row(
                    Modifier
                        .selectable(
                            selected = (dest == selectedOption),
                            onClick = {
                                onOptionSelected(dest)
                            }
                        )
                        .padding(vertical = 2.5.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (dest == selectedOption),
                        onClick = { onOptionSelected(dest) }
                    )
                    Text(
                        text = dest.name,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
