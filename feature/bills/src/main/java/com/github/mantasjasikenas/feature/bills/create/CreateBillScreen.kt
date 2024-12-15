@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.mantasjasikenas.feature.bills.create

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mantasjasikenas.core.common.util.Uid
import com.github.mantasjasikenas.core.domain.model.Destination
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.domain.model.bills.PurchaseBill
import com.github.mantasjasikenas.core.domain.model.bills.Taxes
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.UsersPicker
import com.github.mantasjasikenas.core.ui.component.NamiokaiNumberField
import com.github.mantasjasikenas.core.ui.component.NamiokaiTextField
import com.github.mantasjasikenas.feature.bills.R

private enum class BillType {
    Purchase, Trip, Flat
}

@Composable
fun CreateBillScreen(
    modifier: Modifier = Modifier,
    navigatedFrom: String? = null,
    sharedState: SharedState,
    onNavigateUp: () -> Unit,
    createBillViewModel: CreateBillViewModel = hiltViewModel(),
) {
    val uiState by createBillViewModel.createBillUiState.collectAsStateWithLifecycle()

    when (uiState) {
        CreateBillUiState.Loading -> {
            NamiokaiCircularProgressIndicator()
        }

        is CreateBillUiState.Success -> {
            CreateBillContent(
                modifier = modifier,
                uiState = uiState as CreateBillUiState.Success,
                usersMap = sharedState.usersMap,
                createBillViewModel = createBillViewModel,
                onNavigateUp = onNavigateUp,
                navigatedFrom = navigatedFrom
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBillContent(
    modifier: Modifier = Modifier,
    uiState: CreateBillUiState.Success,
    usersMap: UsersMap,
    createBillViewModel: CreateBillViewModel,
    onNavigateUp: () -> Unit,
    navigatedFrom: String?
) {
    val billType = when (navigatedFrom) {
        "trips" -> BillType.Trip
        "flat" -> BillType.Flat
        else -> BillType.Purchase
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var selectedIndex by remember { mutableIntStateOf(BillType.entries.indexOf(billType)) }
        val options = BillType.entries

        Text(
            text = stringResource(R.string.type),
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 5.dp)
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = { selectedIndex = index },
                    selected = index == selectedIndex
                ) {
                    Text(text = label.name)
                }
            }
        }

        Crossfade(
            targetState = selectedIndex, label = ""
        ) {
            BillContainerWrapper {
                when (BillType.entries[it]) {
                    BillType.Purchase -> {
                        PurchaseBillContent(
                            onSaveClick = {
                                createBillViewModel.insertBill(it)
                                onNavigateUp()
                            },
                            usersMap = usersMap
                        )
                    }

                    BillType.Trip -> {
                        TripBillContent(
                            onSaveClick = {
                                createBillViewModel.insertFuel(it)
                                onNavigateUp()
                            },
                            usersMap = usersMap,
                            destinations = uiState.destinations
                        )
                    }

                    BillType.Flat -> {
                        FlatBillContent(
                            onSaveClick = {
                                createBillViewModel.insertFlatBill(it)
                                onNavigateUp()
                            },
                            usersMap = usersMap,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BillContainerWrapper(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        content()
    }
}

@Composable
private fun UserPickerContainer(
    modifier: Modifier = Modifier,
    title: String,
    usersMap: UsersMap,
    usersSnapshotMap: SnapshotStateMap<Uid, Boolean>,
    isMultipleSelectEnabled: Boolean
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 5.dp)
    )

    UsersPicker(
        usersMap = usersMap,
        usersPickup = usersSnapshotMap,
        isMultipleSelectEnabled = isMultipleSelectEnabled
    )
}

@Composable
fun PurchaseBillContent(
    initialPurchaseBill: PurchaseBill = PurchaseBill(),
    onSaveClick: (PurchaseBill) -> Unit,
    usersMap: UsersMap
) {
    val context = LocalContext.current

    val bill by remember {
        mutableStateOf(initialPurchaseBill)
    }

    val splitBillHashMap = remember {
        usersMap.map { it.value.uid to false }
            .toMutableStateMap()
    }
    val paymasterHashMap = remember {
        usersMap.map { it.value.uid to false }
            .toMutableStateMap()
    }

    LaunchedEffect(Unit) {
        if (bill.isValid()) {
            bill.splitUsersUid.forEach { uid ->
                splitBillHashMap[uid] = true
            }

            bill.paymasterUid.let { uid ->
                paymasterHashMap[uid] = true
            }
        }
    }

    UserPickerContainer(
        title = stringResource(R.string.paymaster),
        usersMap = usersMap,
        usersSnapshotMap = paymasterHashMap,
        isMultipleSelectEnabled = false
    )

    Spacer(modifier = Modifier.height(20.dp))

    UserPickerContainer(
        title = stringResource(R.string.split_bill_with),
        usersMap = usersMap,
        usersSnapshotMap = splitBillHashMap,
        isMultipleSelectEnabled = true
    )

    Spacer(modifier = Modifier.height(20.dp))

    NamiokaiTextField(
        label = stringResource(R.string.shopping_list),
        initialTextFieldValue = bill.shoppingList,
        onValueChange = { bill.shoppingList = it },
        leadingIcon = {
            Icon(
                modifier = Modifier.size(21.dp),
                imageVector = Icons.Outlined.ShoppingBag,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )

    Spacer(modifier = Modifier.height(20.dp))

    NamiokaiNumberField(
        label = stringResource(R.string.total_price),
        initialTextFieldValue = (if (bill.total == 0.0) "" else bill.total.toString()),
        onValueChange = {
            bill.total = it
        },
        leadingIcon = {
            Icon(
                modifier = Modifier.size(21.dp),
                imageVector = Icons.Outlined.EuroSymbol,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )

    Spacer(modifier = Modifier.height(32.dp))

    Button(onClick = {
        bill.splitUsersUid = splitBillHashMap.filter { it.value }.keys.map { it }
        bill.paymasterUid = paymasterHashMap.filter { it.value }.keys.map { it }
            .firstOrNull() ?: ""

        if (!bill.isValid()) {
            Toast.makeText(
                context,
                R.string.please_fill_all_fields,
                Toast.LENGTH_SHORT
            )
                .show()
            return@Button
        }

        onSaveClick(bill)

        Toast.makeText(
            context,
            R.string.bill_saved,
            Toast.LENGTH_SHORT
        )
            .show()
    }) {
        Text(text = "Save")
    }
}

@Composable
fun TripBillContent(
    initialTripBill: TripBill = TripBill(),
    onSaveClick: (TripBill) -> Unit,
    usersMap: UsersMap,
    destinations: List<Destination>
) {
    val context = LocalContext.current

    val trip by remember {
        mutableStateOf(initialTripBill)
    }

    // OPTIMIZE: This is a mess, refactor OK?
    val splitFuelHashMap = remember {
        usersMap.map { it.value.uid to false }
            .toMutableStateMap()
    }
    val driverSelectHashMap = remember {
        usersMap.map { it.value.uid to false }
            .toMutableStateMap()
    }

    val (selectedOption, onOptionSelected) = remember { mutableStateOf(destinations[0]) }

    LaunchedEffect(Unit) {
        if (trip.isValid()) {
            trip.splitUsersUid.forEach { uid ->
                splitFuelHashMap[uid] = true
            }

            driverSelectHashMap[trip.paymasterUid] = true
            onOptionSelected(destinations.first { it.name == initialTripBill.tripDestination })
        }

    }

    UserPickerContainer(
        title = stringResource(R.string.driver),
        usersMap = usersMap,
        usersSnapshotMap = driverSelectHashMap,
        isMultipleSelectEnabled = false
    )

    Spacer(modifier = Modifier.height(20.dp))

    UserPickerContainer(
        title = stringResource(R.string.passengers),
        usersMap = usersMap,
        usersSnapshotMap = splitFuelHashMap,
        isMultipleSelectEnabled = true
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = stringResource(R.string.destination),
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(bottom = 5.dp)
    )

    Column {
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

    Spacer(modifier = Modifier.height(32.dp))

    Button(onClick = {
        trip.paymasterUid =
            driverSelectHashMap.filter { it.value }.keys.map { it }
                .firstOrNull() ?: ""
        trip.splitUsersUid = splitFuelHashMap.filter { it.value }.keys.map { it }


        trip.tripDestination = selectedOption.name
        trip.tripPricePerUser = when (trip.splitUsersUid.count()) {
            1 -> selectedOption.tripPriceAlone
            else -> selectedOption.tripPriceWithOthers
        }

        if (!trip.isValid()) {
            Toast.makeText(
                context,
                R.string.please_fill_all_fields,
                Toast.LENGTH_SHORT
            )
                .show()
            return@Button
        }

        onSaveClick(trip)

        Toast.makeText(
            context,
            R.string.fuel_saved,
            Toast.LENGTH_SHORT
        )
            .show()
    }) {
        Text(text = "Save")
    }
}

@Composable
fun FlatBillContent(
    initialFlatBill: FlatBill = FlatBill(),
    onSaveClick: (FlatBill) -> Unit,
    usersMap: UsersMap
) {
    val context = LocalContext.current

    var flatBill by remember {
        mutableStateOf(initialFlatBill)
    }
    val (includeTaxes, onIncludeTaxesChange) = remember { mutableStateOf(false) }

    val paymasterHashMap = remember {
        usersMap.map { it.value.uid to false }
            .toMutableStateMap()
    }
    val splitBillHashMap = remember {
        usersMap.map { it.value.uid to false }
            .toMutableStateMap()
    }

    LaunchedEffect(Unit) {
        if (flatBill.isValid()) {
            paymasterHashMap[flatBill.paymasterUid] = true
            flatBill.splitUsersUid.forEach { splitBillHashMap[it] = true }
        }
    }

    UserPickerContainer(
        title = stringResource(R.string.paymaster),
        usersMap = usersMap,
        usersSnapshotMap = paymasterHashMap,
        isMultipleSelectEnabled = false
    )

    Spacer(modifier = Modifier.height(20.dp))

    UserPickerContainer(
        title = stringResource(R.string.split_bill_with),
        usersMap = usersMap,
        usersSnapshotMap = splitBillHashMap,
        isMultipleSelectEnabled = true
    )

    Spacer(modifier = Modifier.height(20.dp))

    NamiokaiNumberField(
        label = "Rent",
        initialTextFieldValue = (if (flatBill.rentTotal == 0.0) "" else flatBill.rentTotal.toString()),
        onValueChange = { flatBill.rentTotal = it },
        keyboardType = KeyboardType.Number,
        leadingIcon = {
            Icon(
                modifier = Modifier.size(21.dp),
                imageVector = Icons.Outlined.EuroSymbol,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )

    Spacer(modifier = Modifier.height(20.dp))

    NamiokaiNumberField(
        label = "Taxes",
        initialTextFieldValue = (if (flatBill.taxesTotal == 0.0) "" else flatBill.taxesTotal.toString()),
        onValueChange = { flatBill.taxesTotal = it },
        keyboardType = KeyboardType.Number,
        leadingIcon = {
            Icon(
                modifier = Modifier.size(21.dp),
                imageVector = Icons.Outlined.EuroSymbol,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )

    Spacer(modifier = Modifier.height(20.dp))

    Row(
        Modifier
            .fillMaxWidth()
            .height(46.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Checkbox(
            checked = includeTaxes,
            onCheckedChange = {
                val updatedValue = !includeTaxes

                onIncludeTaxesChange(updatedValue)

                flatBill = if (updatedValue) {
                    flatBill.copy(taxes = Taxes())
                } else {
                    flatBill.copy(taxes = null)
                }
            },
        )
        Text(
            text = "Include taxes",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }

    if (includeTaxes && flatBill.taxes != null) {
        Spacer(modifier = Modifier.height(20.dp))

        NamiokaiNumberField(
            label = "Electricity",
            initialTextFieldValue = (if (flatBill.taxes!!.electricity == 0.0) "" else flatBill.taxes!!.electricity.toString()),
            onValueChange = { flatBill.taxes!!.electricity = it },
            keyboardType = KeyboardType.Number,
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(21.dp),
                    imageVector = Icons.Outlined.EuroSymbol,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
    }

    Spacer(modifier = Modifier.height(32.dp))

    Button(onClick = {
        flatBill.splitUsersUid = splitBillHashMap.filter { it.value }.keys.map { it }
        flatBill.paymasterUid = paymasterHashMap.filter { it.value }.keys.map { it }
            .firstOrNull() ?: ""

        if (!flatBill.isValid()) {
            Toast.makeText(
                context,
                R.string.please_fill_all_fields,
                Toast.LENGTH_SHORT
            )
                .show()
            return@Button
        }

        onSaveClick(flatBill)

        Toast.makeText(
            context,
            R.string.bill_saved,
            Toast.LENGTH_SHORT
        )
            .show()
    }) {
        Text(text = "Save")
    }
}
