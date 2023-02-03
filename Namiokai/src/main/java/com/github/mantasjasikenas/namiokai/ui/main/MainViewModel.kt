package com.github.mantasjasikenas.namiokai.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.namiokai.data.AuthRepository
import com.github.mantasjasikenas.namiokai.data.FirebaseRepository
import com.github.mantasjasikenas.namiokai.data.UsersRepository
import com.github.mantasjasikenas.namiokai.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    val authRepository: AuthRepository,
    val firebaseRepository: FirebaseRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _mainUiState = MutableStateFlow(MainUiState())
    val mainUiState = _mainUiState.asStateFlow()


    init {
        Log.d(TAG, "MainViewModel init")
        getUsersFromDatabase()
        getCurrentUserDetails()
    }

    private fun getUsersFromDatabase() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                usersRepository.getUsers().collect { users ->
                    val usersMap = users.associateBy { it.uid }
                    _mainUiState.update { it.copy(usersMap = usersMap) }
                }
            }
        }
    }

    fun getCurrentUserDetails() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Firebase.auth.currentUser?.uid?.let { uid ->
                    firebaseRepository.getUser(uid).collect { user ->
                        println(uid)
                        println(user)
                        _mainUiState.update { it.copy(currentUser = user) }
                        Log.d(TAG, "Is admin? ${user.admin}")
                    }
                }
            }

        }
    }

    fun resetCurrentUser() {
        _mainUiState.update {
            it.copy(currentUser = User())
        }
    }
}