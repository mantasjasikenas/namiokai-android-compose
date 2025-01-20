package com.github.mantasjasikenas.feature.debts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mantasjasikenas.core.domain.model.period.Period
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator

@Composable
fun DebtsRoute(
    modifier: Modifier = Modifier, viewModel: DebtsViewModel = hiltViewModel()
) {
    DebtsScreen(
        debtsViewModel = viewModel
    )
}

@Composable
fun DebtsScreen(
    debtsViewModel: DebtsViewModel = hiltViewModel(),
) {
    val debtsUiState by debtsViewModel.debtsUiState.collectAsStateWithLifecycle()
    val periodOffset by debtsViewModel.periodOffset.collectAsStateWithLifecycle()

    when (debtsUiState) {
        is DebtsUiState.Loading -> {
            NamiokaiCircularProgressIndicator()
        }

        is DebtsUiState.Success -> {
            DebtsScreenContent(
                debtsUiState = debtsUiState as DebtsUiState.Success,
                periodOffset = periodOffset,
                onPeriodReset = debtsViewModel::onPeriodReset,
                onPeriodUpdate = debtsViewModel::onPeriodUpdate,
                onPeriodOffsetUpdate = debtsViewModel::onPeriodOffsetUpdate,
            )
        }
    }
}

@Composable
fun DebtsScreenContent(
    debtsUiState: DebtsUiState.Success,
    periodOffset: Int,
    onPeriodReset: () -> Unit,
    onPeriodUpdate: (Period) -> Unit,
    onPeriodOffsetUpdate: (Int) -> Unit,
) {
    DebtsPage(
        spacesDebts = debtsUiState.spacesDebts,
        usersMap = debtsUiState.usersMap,
        periodOffset = periodOffset,
        onPeriodReset = onPeriodReset,
        onPeriodUpdate = onPeriodUpdate,
        onPeriodOffsetUpdate = onPeriodOffsetUpdate
    )
}








