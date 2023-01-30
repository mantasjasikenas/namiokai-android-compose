package com.example.namiokai.ui.screens.fuel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.namiokai.data.FirebaseRepository
import com.example.namiokai.model.Fuel
import com.example.namiokai.utils.Constants.DATE_FORMAT_DISPLAY
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
        getDestinations()
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

    private fun getDestinations() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.getDestinations().collect { destinations ->
                    _fuelUiState.update { it.copy(destinations = destinations) }
                }
            }
        }
    }

    fun insertFuel(fuel: Fuel) {
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_DISPLAY)
        val currentDateTime = LocalDateTime.now().format(formatter)

        fuel.date = currentDateTime

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.insertFuel(fuel)
            }
        }
    }

}