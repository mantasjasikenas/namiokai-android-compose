package com.github.mantasjasikenas.feature.debts.pages

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.core.common.util.UserUid
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.domain.model.PeriodState
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.debts.DebtBill
import com.github.mantasjasikenas.core.domain.model.period.Period
import com.github.mantasjasikenas.core.ui.common.EuroIconTextRow
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.component.NamiokaiElevatedOutlinedCard
import com.github.mantasjasikenas.core.ui.component.NamiokaiOutlinedCard
import com.github.mantasjasikenas.core.ui.component.NoResultsFound
import com.github.mantasjasikenas.core.ui.component.SwipePeriod

@Composable
internal fun PersonalDebts(
    currentUserDebts: Map<UserUid, List<DebtBill>>?,
    usersMap: UsersMap,
) {
    if (currentUserDebts == null) {
        return
    }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val launchSwedbank = {
        val launchIntent: Intent? =
            context.packageManager.getLaunchIntentForPackage("lt.swedbank.mobile")

        if (launchIntent != null) {
            context.startActivity(launchIntent)
        }
    }

    NamiokaiSpacer(height = 20)

    NamiokaiOutlinedCard {

        currentUserDebts.forEach { (uid, debtBills) ->
            val value = remember(uid, debtBills) {
                debtBills.sumOf { it.amount }
            }

            EuroIconTextRow(
                label = usersMap[uid]!!.displayName,
                value = value.format(2),
                onLongClick = {
                    clipboardManager.setText(AnnotatedString(value.format(2)))
                    launchSwedbank()
                }
            )
        }

        if ((currentUserDebts.size) > 1) {
            val total = remember(currentUserDebts) {
                currentUserDebts.values.flatten().sumOf { it.amount }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 3.dp),
                thickness = 2.dp
            )
            EuroIconTextRow(
                label = "Total",
                value = total.format(2),
                onLongClick = {
                    clipboardManager.setText(AnnotatedString(total.format(2)))
                }
            )
        }
    }
}

@Composable
private fun PersonalDebtsPage(
    usersMap: UsersMap,
    periodState: PeriodState,
    currentUserDebts: Map<UserUid, List<DebtBill>>?,
    onPeriodReset: () -> Unit,
    onPeriodUpdate: (Period) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            NamiokaiElevatedOutlinedCard {
                Text(
                    text = "Your debts",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                SwipePeriod(
                    periods = periodState.periods,
                    selectedPeriod = periodState.userSelectedPeriod,
                    appPeriod = periodState.currentPeriod,
                    onPeriodReset = onPeriodReset,
                    onPeriodUpdate = onPeriodUpdate,
                )
            }

            if (currentUserDebts.isNullOrEmpty()) {
                NoDebtsFound()
            } else {
                PersonalDebts(
                    currentUserDebts = currentUserDebts,
                    usersMap = usersMap
                )
            }
        }
    }
}

@Composable
fun NoDebtsFound(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NoResultsFound(
            label = "No debts was found.\nYou are all good!",
            modifier = modifier
        )
    }
}