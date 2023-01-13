package com.example.namiokai.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.namiokai.data.AuthRepository
import com.example.namiokai.data.FirebaseRepository
import com.example.namiokai.data.UsersRepository
import com.example.namiokai.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val authRepository: AuthRepository,
    val firebaseRepository: FirebaseRepository,
    private val usersRepository: UsersRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _mainUiState = MutableStateFlow(MainUiState())
    val uiState = _mainUiState.asStateFlow()

    init {
        getUsers()
    }

    private fun getUsers() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                usersRepository.getUsers().collect { users ->
                    _mainUiState.update { it.copy(users = users) }
                }
            }
        }
    }


    // TODO Implement admin claim
    private fun getUser() {
        if (!authRepository.isUserAuthenticatedInFirebase)
            return

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.user?.let { userRepository ->
                    firebaseRepository.getUser(userRepository.uid).collect { user ->
                        _mainUiState.update { it.copy(user = user) }
                    }
                }
            }

        }
    }
}