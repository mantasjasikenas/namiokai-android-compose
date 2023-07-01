package com.github.mantasjasikenas.namiokai.ui.screens.test

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.mantasjasikenas.namiokai.data.BaseFirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private const val TAG = "TestViewModel"

@HiltViewModel
class TestViewModel @Inject constructor(private val repo: BaseFirebaseRepository) :
    ViewModel() {

    private val _testUiState = MutableStateFlow(TestUiState())
    val testUiState = _testUiState.asStateFlow()

    init {
        Log.d(TAG, "Initiated")
    }

}