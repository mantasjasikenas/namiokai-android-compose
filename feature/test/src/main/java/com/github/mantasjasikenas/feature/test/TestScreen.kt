package com.github.mantasjasikenas.feature.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TestRoute() {
    TestScreen()
}

@Composable
fun TestScreen(
    viewModel: TestViewModel = hiltViewModel()
) {
    val state by viewModel.testUiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Test screen")
        Text(text = state.text)
    }
}
