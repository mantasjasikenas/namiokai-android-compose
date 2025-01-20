@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.mantasjasikenas.feature.debts

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mantasjasikenas.core.domain.model.period.Period
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.PagesFlowRow
import com.github.mantasjasikenas.core.ui.common.rememberState
import com.github.mantasjasikenas.feature.debts.pages.DebtsPage

@Composable
fun DebtsRoute(
    modifier: Modifier = Modifier,
    viewModel: DebtsViewModel = hiltViewModel()
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

    when (debtsUiState) {
        is DebtsUiState.Loading -> {
            NamiokaiCircularProgressIndicator()
        }

        is DebtsUiState.Success -> {
            DebtsScreenContent(
                debtsUiState = debtsUiState as DebtsUiState.Success,
                onPeriodReset = debtsViewModel::onPeriodReset,
                onPeriodUpdate = debtsViewModel::onPeriodUpdate,
            )
        }
    }
}

@Composable
fun DebtsScreenContent(
    debtsUiState: DebtsUiState.Success,
    onPeriodReset: () -> Unit,
    onPeriodUpdate: (Period) -> Unit,
) {
    val usersMap = remember(debtsUiState.users) {
        debtsUiState.users.associateBy { it.uid }
    }

    val pages = listOf(
        "Personal",
        "All"
    )
    var currentPage by rememberState {
        1
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            PagesFlowRow(
                pages = pages,
                currentPage = currentPage,
                onPageClick = {
                    currentPage = it
                }
            )
        }

        AnimatedContent(
            targetState = currentPage,
            label = "",
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { height -> height } + fadeIn()).togetherWith(
                        slideOutHorizontally { height -> -height } + fadeOut())
                } else {
                    (slideInHorizontally { height -> -height } + fadeIn()).togetherWith(
                        slideOutHorizontally { height -> height } + fadeOut())
                }.using(
                    SizeTransform(clip = false)
                )
            },
        ) {
            when (it) {
                /*  0 -> {
                      PersonalDebtsPage(
                          periodState = debtsUiState.periodState,
                          currentUserDebts = debtsUiState.currentUserDebts,
                          onPeriodReset = onPeriodReset,
                          onPeriodUpdate = onPeriodUpdate,
                          usersMap = usersMap
                      )
                  }*/

                1 -> {
                    DebtsPage(
                        spacesDebts = debtsUiState.spacesDebts,
                        usersMap = usersMap,
                        onPeriodReset = onPeriodReset,
                        onPeriodUpdate = onPeriodUpdate,
                    )
                }
            }

        }
    }
}








