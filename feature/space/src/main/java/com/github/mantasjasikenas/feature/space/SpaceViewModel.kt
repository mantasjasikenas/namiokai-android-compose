package com.github.mantasjasikenas.feature.space

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.domain.model.Space
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.repository.SpaceRepository
import com.github.mantasjasikenas.core.domain.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SpaceViewModel @Inject constructor(
    usersRepository: UsersRepository,
    private val spaceRepository: SpaceRepository
) :
    ViewModel() {

    val spaceUiState: StateFlow<SpaceUiState> =
        usersRepository.currentUser
            .flatMapLatest { currentUser ->
                spaceRepository.getSpacesByUser(currentUser.uid)
                    .map { spaces ->
                        SpaceUiState.Success(currentUser = currentUser, spaces = spaces)
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SpaceUiState.Loading
            )

    fun deleteSpace(spaceId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                spaceRepository.deleteSpace(spaceId)
            }
        }
    }
}

sealed interface SpaceUiState {
    data object Loading : SpaceUiState
    data class Success(
        val currentUser: User = User(),
        val spaces: List<Space> = emptyList(),
    ) : SpaceUiState
}