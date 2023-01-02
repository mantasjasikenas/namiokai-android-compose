package com.example.namiokai.ui.screens.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.namiokai.data.NamiokaiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val repo: NamiokaiRepository
) : ViewModel() {

    private val _summaryUiState = MutableStateFlow(SummaryUiState())
    val uiState = _summaryUiState.asStateFlow()

    init {
        viewModelScope.launch {
            getUsers()
        }
    }

    private suspend fun getUsers() {
        _summaryUiState.update {
            it.copy(users = repo.getUsers())
        }

        /*        viewModelScope.launch {
                    amphibiansUiState = AmphibiansUiState.Loading
                    amphibiansUiState = try {
                        AmphibiansUiState.Success(amphibiansRepository.getAmphibians())
                    } catch (e: IOException) {
                        AmphibiansUiState.Error
                    } catch (e: HttpException) {
                        AmphibiansUiState.Error
                    }
                }*/

    }

}
