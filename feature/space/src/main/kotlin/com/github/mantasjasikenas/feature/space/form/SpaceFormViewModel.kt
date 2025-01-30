package com.github.mantasjasikenas.feature.space.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.mantasjasikenas.core.domain.model.space.Space
import com.github.mantasjasikenas.core.domain.repository.SpaceRepository
import com.github.mantasjasikenas.feature.space.navigation.SpaceFormRoute
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SpaceFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val spaceRepository: SpaceRepository
) : ViewModel() {

    val spaceFormRoute = savedStateHandle.toRoute<SpaceFormRoute>()

    val spaceFormUiState: StateFlow<SpaceFormUiState> =
        spaceFlow()
            .map { space ->
                SpaceFormUiState.Success(initialSpace = space)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SpaceFormUiState.Loading
            )

    private fun spaceFlow() = flow {
        if (spaceFormRoute.spaceId == null) {
            emit(null)

            return@flow
        }

        val spaceFlow = spaceRepository
            .getSpace(spaceFormRoute.spaceId)

        emitAll(spaceFlow)
    }

    fun insertSpace(space: Space) {
        space.createdBy = Firebase.auth.uid ?: ""

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                spaceRepository.createSpace(space)
            }
        }
    }

    fun updateSpace(space: Space) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                spaceRepository.updateSpace(space)
            }
        }
    }
}

sealed interface SpaceFormUiState {
    data object Loading : SpaceFormUiState
    data class Success(
        val initialSpace: Space? = null
    ) : SpaceFormUiState
}