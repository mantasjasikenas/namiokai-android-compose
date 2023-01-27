package com.example.namiokai.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.namiokai.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel(){

    fun logout(){
        viewModelScope.launch {
            userRepository.signOut()
        }
    }
}