package com.github.mantasjasikenas.namiokai.ui.screens.fuel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.namiokai.data.FirebaseRepository
import com.github.mantasjasikenas.namiokai.model.Fuel
import com.github.mantasjasikenas.namiokai.utils.Constants.DATE_TIME_FORMAT
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
        val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
        val currentDateTime = LocalDateTime.now().format(formatter)

        fuel.date = currentDateTime
        fuel.createdByUid = Firebase.auth.uid ?: ""

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.insertFuel(fuel)
            }
        }
    }

    fun updateFuel(fuel: Fuel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.updateFuel(fuel)
            }
        }
    }

    fun deleteFuel(fuel: Fuel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.deleteFuel(fuel)
            }
        }

    }

}