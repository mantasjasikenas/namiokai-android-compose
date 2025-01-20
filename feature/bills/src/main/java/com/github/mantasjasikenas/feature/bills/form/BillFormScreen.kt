@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalLayoutApi::class
)

package com.github.mantasjasikenas.feature.bills.form

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.outlined.Workspaces
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mantasjasikenas.core.common.util.Uid
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.Space
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.Bill
import com.github.mantasjasikenas.core.domain.model.bills.BillType
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.domain.model.bills.PurchaseBill
import com.github.mantasjasikenas.core.domain.model.bills.Taxes
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.UsersPicker
import com.github.mantasjasikenas.core.ui.component.NamiokaiDropdownMenu
import com.github.mantasjasikenas.core.ui.component.NamiokaiNumberField
import com.github.mantasjasikenas.core.ui.component.NamiokaiTextField
import com.github.mantasjasikenas.core.ui.component.NoResultsFound
import com.github.mantasjasikenas.feature.bills.R
import com.github.mantasjasikenas.feature.bills.navigation.BillFormRoute

@Composable
fun BillFormRoute(
    sharedState: SharedState, onNavigateUp: () -> Unit
) {
    BillFormScreen(
        sharedState = sharedState, onNavigateUp = onNavigateUp
    )
}

@Composable
fun BillFormScreen(
    modifier: Modifier = Modifier,
    sharedState: SharedState,
    onNavigateUp: () -> Unit,
    billFormViewModel: BillFormViewModel = hiltViewModel(),
) {
    val uiState by billFormViewModel.billFormUiState.collectAsStateWithLifecycle()

    when (uiState) {
        BillFormUiState.Loading -> {
            NamiokaiCircularProgressIndicator()
        }

        is BillFormUiState.Success -> {
            BillFormContent(
                modifier = modifier,
                uiState = uiState as BillFormUiState.Success,
                usersMap = sharedState.usersMap,
                billFormViewModel = billFormViewModel,
                billFormRoute = billFormViewModel.billFormRoute,
                onNavigateUp = onNavigateUp,
            )
        }
    }
}

@Composable
fun BillFormContent(
    modifier: Modifier = Modifier,
    uiState: BillFormUiState.Success,
    usersMap: UsersMap,
    billFormViewModel: BillFormViewModel,
    billFormRoute: BillFormRoute,
    onNavigateUp: () -> Unit,
) {
    if (uiState.spaces.isEmpty()) {
        NoResultsFound(label = "No spaces found.\nPlease create a space first to add a bill.")
        return
    }

    val initialBill = uiState.initialBill

    val billType = when (initialBill) {
        is PurchaseBill -> BillType.Purchase
        is TripBill -> BillType.Trip
        is FlatBill -> BillType.Flat
        else -> {
            billFormRoute.billType ?: BillType.Purchase
        }
    }

    val onSaveBill = { bill: Bill ->
        if (initialBill == null) {
            billFormViewModel.insertBill(bill)
        } else {
            billFormViewModel.updateBill(bill)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var selectedIndex by remember(billType) {
            mutableIntStateOf(
                BillType.entries.indexOf(
                    billType
                )
            )
        }
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
                    selected = index == selectedIndex,
                    enabled = initialBill == null
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
                            initialPurchaseBill = initialBill as? PurchaseBill, onSaveClick = {
                                onSaveBill(it)
                                onNavigateUp()
                            }, usersMap = usersMap, spaces = uiState.spaces
                        )
                    }

                    BillType.Trip -> {
                        TripBillContent(
                            initialTripBill = initialBill as? TripBill,
                            onSaveClick = {
                                onSaveBill(it)
                                onNavigateUp()
                            },
                            usersMap = usersMap,
                            spaces = uiState.spaces
                        )
                    }

                    BillType.Flat -> {
                        FlatBillContent(
                            initialFlatBill = initialBill as? FlatBill, onSaveClick = {
                                onSaveBill(it)
                                onNavigateUp()
                            }, usersMap = usersMap, spaces = uiState.spaces
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BillContainerWrapper(
    modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        content()
    }
}

@Composable
fun PurchaseBillContent(
    initialPurchaseBill: PurchaseBill? = null,
    onSaveClick: (PurchaseBill) -> Unit,
    usersMap: UsersMap,
    spaces: List<Space>
) {
    val context = LocalContext.current

    val bill by remember(initialPurchaseBill) {
        mutableStateOf(initialPurchaseBill ?: PurchaseBill())
    }

    val selectedSpace = remember {
        mutableStateOf(spaces.firstOrNull { it.spaceId == bill.spaceId } ?: spaces.firstOrNull())
    }

    SpaceContainer(
        selectedSpace = selectedSpace.value,
        paymasterUid = bill.paymasterUid,
        splitUsersUids = bill.splitUsersUid,
        spaces = spaces,
        usersMap = usersMap,
        paymasterTitle = stringResource(R.string.paymaster),
        splitUsersTitle = stringResource(R.string.split_bill_with),
        onSpaceSelected = { space ->
            selectedSpace.value = space
            bill.spaceId = space.spaceId
            bill.paymasterUid = ""
            bill.splitUsersUid = emptyList()
        },
        onPaymasterSelected = {
            bill.paymasterUid = it
        },
        onSplitUsersSelected = {
            bill.splitUsersUid = it
        })

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
        })

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
        })

    Spacer(modifier = Modifier.height(32.dp))

    Button(onClick = {
        bill.spaceId = selectedSpace.value?.spaceId ?: ""

        if (!bill.isValid()) {
            Toast.makeText(
                context, R.string.please_fill_all_fields, Toast.LENGTH_SHORT
            ).show()
            return@Button
        }

        onSaveClick(bill)

        Toast.makeText(
            context, R.string.bill_saved, Toast.LENGTH_SHORT
        ).show()
    }) {
        Text(text = if (initialPurchaseBill == null) "Save" else "Update")
    }
}

@Composable
private fun TripBillContent(
    initialTripBill: TripBill? = null,
    onSaveClick: (TripBill) -> Unit,
    usersMap: UsersMap,
    spaces: List<Space>
) {
    val context = LocalContext.current

    val trip by remember(initialTripBill) { mutableStateOf(initialTripBill ?: TripBill()) }

    var selectedSpace by remember {
        mutableStateOf(spaces.firstOrNull { it.spaceId == trip.spaceId } ?: spaces.firstOrNull())
    }

    val (selectedDestination, onSelectedDestination) = remember(selectedSpace) {
        mutableStateOf(selectedSpace?.destinations?.firstOrNull { it.name == trip.tripDestination }
            ?: selectedSpace?.destinations?.firstOrNull())
    }

    SpaceContainer(
        selectedSpace = selectedSpace,
        paymasterUid = trip.paymasterUid,
        splitUsersUids = trip.splitUsersUid,
        spaces = spaces,
        usersMap = usersMap,
        paymasterTitle = stringResource(R.string.driver),
        splitUsersTitle = stringResource(R.string.passengers),
        onSpaceSelected = { space ->
            selectedSpace = space

            trip.spaceId = space.spaceId
            trip.paymasterUid = ""
            trip.splitUsersUid = emptyList()
            trip.tripDestination = ""
            trip.tripPricePerUser = 0.0
        },
        onPaymasterSelected = {
            trip.paymasterUid = it
        },
        onSplitUsersSelected = {
            trip.splitUsersUid = it
        })

    Text(
        text = stringResource(R.string.destination),
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(bottom = 5.dp)
    )

    Column {
        if (selectedSpace == null) {
            Text(
                text = "Please select space first",
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
        } else {
            selectedSpace?.destinations?.takeIf { it.isNotEmpty() } ?: run {
                Text(
                    text = "No destinations found!",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                return@Column
            }

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.Center
            ) {
                selectedSpace?.destinations?.forEach { dest ->
                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = (dest == selectedDestination),
                                onClick = { onSelectedDestination(dest) },
                                role = Role.RadioButton
                            ),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (dest == selectedDestination),
                            onClick = null
                        )
                        Text(
                            text = dest.name,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(32.dp))

    Button(onClick = {
        if (selectedDestination == null) {
            Toast.makeText(
                context, "Please select destination first", Toast.LENGTH_SHORT
            ).show()
            return@Button
        }

        trip.spaceId = selectedSpace?.spaceId ?: ""
        trip.tripDestination = selectedDestination.name
        trip.tripPricePerUser = when (trip.splitUsersUid.count()) {
            1 -> selectedDestination.tripPriceAlone
            else -> selectedDestination.tripPriceWithOthers
        }

        if (!trip.isValid()) {
            Toast.makeText(
                context, R.string.please_fill_all_fields, Toast.LENGTH_SHORT
            ).show()
            return@Button
        }

        onSaveClick(trip)

        Toast.makeText(
            context, R.string.fuel_saved, Toast.LENGTH_SHORT
        ).show()
    }) {
        Text(text = if (initialTripBill == null) "Save" else "Update")
    }
}

@Composable
private fun FlatBillContent(
    initialFlatBill: FlatBill? = null,
    onSaveClick: (FlatBill) -> Unit,
    usersMap: UsersMap,
    spaces: List<Space>
) {
    val context = LocalContext.current

    var flatBill by remember(initialFlatBill) {
        mutableStateOf(initialFlatBill ?: FlatBill())
    }
    val selectedSpace = remember {
        mutableStateOf(spaces.firstOrNull { it.spaceId == flatBill.spaceId }
            ?: spaces.firstOrNull())
    }
    val (includeTaxes, onIncludeTaxesChange) = remember { mutableStateOf(false) }

    SpaceContainer(
        selectedSpace = selectedSpace.value,
        paymasterUid = flatBill.paymasterUid,
        splitUsersUids = flatBill.splitUsersUid,
        spaces = spaces,
        usersMap = usersMap,
        paymasterTitle = stringResource(R.string.paymaster),
        splitUsersTitle = stringResource(R.string.split_bill_with),
        onSpaceSelected = { space ->
            selectedSpace.value = space
            flatBill.spaceId = space.spaceId
            flatBill.paymasterUid = ""
            flatBill.splitUsersUid = emptyList()
        },
        onPaymasterSelected = {
            flatBill.paymasterUid = it
        },
        onSplitUsersSelected = {
            flatBill.splitUsersUid = it
        })

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
        })

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
        })

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
            })
    }

    Spacer(modifier = Modifier.height(32.dp))

    Button(onClick = {
        flatBill.spaceId = selectedSpace.value?.spaceId ?: ""

        if (!flatBill.isValid()) {
            Toast.makeText(
                context, R.string.please_fill_all_fields, Toast.LENGTH_SHORT
            ).show()
            return@Button
        }

        onSaveClick(flatBill)

        Toast.makeText(
            context, R.string.bill_saved, Toast.LENGTH_SHORT
        ).show()
    }) {
        Text(text = if (initialFlatBill == null) "Save" else "Update")
    }
}

@Composable
private fun SpaceContainer(
    selectedSpace: Space?,
    paymasterUid: String,
    splitUsersUids: List<String>,
    spaces: List<Space>,
    usersMap: UsersMap,
    paymasterTitle: String = stringResource(R.string.paymaster),
    splitUsersTitle: String = stringResource(R.string.split_bill_with),
    onSpaceSelected: (Space) -> Unit,
    onPaymasterSelected: (Uid) -> Unit,
    onSplitUsersSelected: (List<Uid>) -> Unit
) {
    val spacesMembers = remember(selectedSpace) {
        selectedSpace?.memberIds?.let { memberIds ->
            usersMap.filterKeys { memberIds.contains(it) }
        } ?: emptyMap()
    }

    Text(
        text = "Space",
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 7.dp)
    )

    NamiokaiDropdownMenu(
        label = "Space",
        items = spaces,
        initialSelectedItem = spaces.firstOrNull { it.spaceId == selectedSpace?.spaceId }
            ?: selectedSpace,
        onItemSelected = {
            onSpaceSelected(it)
//            selectedSpace = it
        },
        leadingIconVector = Icons.Outlined.Workspaces,
        itemLabel = { it.spaceName },
    )

    Spacer(modifier = Modifier.height(20.dp))

    AnimatedVisibility(
        modifier = Modifier.fillMaxWidth(), visible = selectedSpace != null
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserPickerContainer(
                title = paymasterTitle,
                usersMap = spacesMembers,
                isMultipleSelectEnabled = false,
                onUsersSelected = { selectedUsers ->
                    selectedUsers.firstOrNull()?.let { onPaymasterSelected(it) }
                },
                initialSelectedUsers = listOf(paymasterUid)
            )

            Spacer(modifier = Modifier.height(20.dp))

            UserPickerContainer(
                title = splitUsersTitle,
                usersMap = spacesMembers,
                isMultipleSelectEnabled = true,
                initialSelectedUsers = splitUsersUids,
                onUsersSelected = { selectedUsers ->
                    onSplitUsersSelected(selectedUsers)
                },
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun UserPickerContainer(
    title: String,
    usersMap: UsersMap,
    isMultipleSelectEnabled: Boolean,
    onUsersSelected: (List<Uid>) -> Unit,
    initialSelectedUsers: List<Uid> = emptyList()
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 5.dp)
    )

    UsersPicker(
        usersMap = usersMap,
        isMultipleSelectEnabled = isMultipleSelectEnabled,
        onUsersSelected = onUsersSelected,
        initialSelectedUsers = initialSelectedUsers
    )
}