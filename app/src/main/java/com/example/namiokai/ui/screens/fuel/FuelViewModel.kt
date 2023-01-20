package com.example.namiokai.ui.screens.fuel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.namiokai.data.FirebaseRepository
import com.example.namiokai.model.Fuel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class FuelViewModel @Inject constructor(private val firebaseRepository: FirebaseRepository) :
    ViewModel() {

    private val _fuelUiState = MutableStateFlow(FuelUiState())
    val uiState = _fuelUiState.asStateFlow()

    init {
        getFuel()
    }

    private fun getFuel() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.getFuel().collect { fuels ->
                    _fuelUiState.update { it.copy(fuels = fuels) }
                }
            }
        }
    }

    fun insertFuel(fuel: Fuel) {

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val currentDateTime = LocalDateTime.now().format(formatter)

        fuel.date = currentDateTime

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.insertFuel(fuel)
            }
        }
    }

}