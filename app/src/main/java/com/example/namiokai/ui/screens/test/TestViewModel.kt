package com.example.namiokai.ui.screens.test


import androidx.lifecycle.ViewModel
import com.example.namiokai.data.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private const val TAG = "TEST"

@HiltViewModel
class TestViewModel @Inject constructor(private val firebaseRepository: FirebaseRepository) :
    ViewModel() {
    private val _testUiState = MutableStateFlow(TestUiState())
    val testUiState = _testUiState.asStateFlow()


}