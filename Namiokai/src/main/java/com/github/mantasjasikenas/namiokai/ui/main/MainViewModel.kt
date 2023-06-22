package com.github.mantasjasikenas.namiokai.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.namiokai.data.AuthRepository
import com.github.mantasjasikenas.namiokai.data.FirebaseRepository
import com.github.mantasjasikenas.namiokai.data.UsersRepository
import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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
        addConfigUpdateListener()
    }

    private fun getUsersFromDatabase() {
        if (Firebase.auth.currentUser == null) {
            return
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                usersRepository.getUsers().collect { users ->
                    val usersMap = users.associateBy { it.uid }
                    _mainUiState.update { it.copy(usersMap = usersMap) }
                }
            }
        }
    }

    private fun getCurrentUserDetails() {
        if (Firebase.auth.currentUser == null) {
            return
        }

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

    fun fetchData() {
        getCurrentUserDetails()
        getUsersFromDatabase()
    }

    fun getCurrentPeriod(): Period {


        val startDayInclusive = 25

        val periodStart: LocalDate
        val periodEnd: LocalDate

        val currentDate = Clock.System.now().toLocalDateTime(
            TimeZone.currentSystemDefault()
        ).date

        if (currentDate.dayOfMonth < startDayInclusive) {
            periodStart = LocalDate(currentDate.year, currentDate.monthNumber - 1, startDayInclusive)
            periodEnd = LocalDate(currentDate.year, currentDate.monthNumber, startDayInclusive - 1)
        } else {
            periodStart = LocalDate(currentDate.year, currentDate.monthNumber, startDayInclusive)
            periodEnd = LocalDate(currentDate.year, currentDate.monthNumber + 1, startDayInclusive - 1)
        }

        return Period(periodStart, periodEnd)
    }

    private fun addConfigUpdateListener() {

        Firebase.remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d(TAG, "Updated keys: " + configUpdate.updatedKeys)

                if (configUpdate.updatedKeys.contains("period_start_day")) {
                    Firebase.remoteConfig.activate().addOnCompleteListener {
                        Log.d(TAG, "Remote config updated")
                        val value = Firebase.remoteConfig.getValue("period_start_day").asString()
                        Log.d(TAG, "New value: $value")

                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG, "Config update error with code: " + error.code, error)
            }
        })

    }
}