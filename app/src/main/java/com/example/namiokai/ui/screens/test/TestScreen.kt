package com.example.namiokai.ui.screens.test

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.namiokai.R

@Composable
fun TestScreen(
    viewModel: TestViewModel = hiltViewModel()
) {
    val state by viewModel.testUiState.collectAsState()

    Column {
        Button(
            content = { Text(text = stringResource(R.string.save)) },
            onClick = {})
    }
}
