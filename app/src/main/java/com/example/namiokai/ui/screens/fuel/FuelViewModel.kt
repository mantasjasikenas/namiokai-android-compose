package com.example.namiokai.ui.screens.fuel

import android.annotation.SuppressLint
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
import java.text.SimpleDateFormat
import java.util.Date
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

    //"MM-dd-yyyy HH:mm:ss"
    @SuppressLint("SimpleDateFormat")
    fun insertFuel(fuel: Fuel) {

        val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss:ms")
        val currentDate = format.format(Date())

        fuel.date = currentDate

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.insertFuel(fuel)
            }
        }
    }

}