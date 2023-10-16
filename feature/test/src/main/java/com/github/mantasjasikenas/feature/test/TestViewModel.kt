package com.github.mantasjasikenas.feature.test

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private const val TAG = "TestViewModel"

@HiltViewModel
class TestViewModel @Inject constructor() :
    ViewModel() {

    private val _testUiState = MutableStateFlow(TestUiState())
    val testUiState = _testUiState.asStateFlow()

    init {
        Log.d(
            TAG,
            "Initiated"
        )
    }

}

data class TestUiState(val text: String = "")