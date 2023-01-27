package com.example.namiokai.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.namiokai.data.AuthRepository
import com.example.namiokai.data.FirebaseRepository
import com.example.namiokai.data.UsersRepository
import com.example.namiokai.model.User
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

const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    val authRepository: AuthRepository,
    val firebaseRepository: FirebaseRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _mainUiState = MutableStateFlow(MainUiState())
    val uiState = _mainUiState.asStateFlow()

    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()

    init {
        Log.d(TAG, "MainViewModel init")
        getUsersFromDatabase()
        getCurrentUserDetails()
    }

    private fun getUsersFromDatabase() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                usersRepository.getUsers().collect { users ->
                    _mainUiState.update { it.copy(users = users) }
                }
            }
        }
    }

    fun getCurrentUserDetails() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Firebase.auth.currentUser?.uid?.let {uid ->
                    firebaseRepository.getUser(uid).collect { user ->
                        println(uid)
                        println(user)
                        _currentUser.update { user }
                        Log.d(TAG, "Is admin? ${user.admin}")
                    }
                }
            }

        }
    }

    fun resetCurrentUser() {
        _currentUser.update {
            User()
        }
    }
}