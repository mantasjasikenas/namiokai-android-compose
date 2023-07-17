package com.github.mantasjasikenas.namiokai.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.namiokai.data.BaseFirebaseRepository
import com.github.mantasjasikenas.namiokai.data.UsersRepository
import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.model.getMonthlyPeriod
import com.github.mantasjasikenas.namiokai.model.previousMonthly
import com.github.mantasjasikenas.namiokai.utils.toUser
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
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    val baseFirebaseRepository: BaseFirebaseRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _mainUiState = MutableStateFlow(MainUiState(currentUser = getUserFromAuth()))
    val mainUiState = _mainUiState.asStateFlow()

    private val _periodState = MutableStateFlow(
        PeriodUiState(
            currentPeriod = Period.getMonthlyPeriod(getStartDate()),
            userSelectedPeriod = Period.getMonthlyPeriod(getStartDate())
        )
    )
    val periodState = _periodState.asStateFlow()


    init {
        setLoadingStatus(true)
        Log.d(
            TAG,
            "MainViewModel init"
        )

        getUsersFromDatabase()
        getCurrentUserDetails()
        addConfigUpdateListener()

        setLoadingStatus(false)
    }

    fun init() {
        Log.d(
            TAG,
            "init called"
        )
    }

    fun updateUserSelectedPeriodState(period: Period) {
        _periodState.update { it.copy(userSelectedPeriod = period) }
    }

    private fun setLoadingStatus(isLoading: Boolean) {
        if (_mainUiState.value.isLoading == isLoading) {
            return
        }
        _mainUiState.update { it.copy(isLoading = isLoading) }
    }

    private fun getUserFromAuth() = Firebase.auth.currentUser?.toUser() ?: User()

    private fun getUsersFromDatabase() {
        if (Firebase.auth.currentUser == null) {
            return
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                usersRepository.getUsers()
                    .collect { users ->
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
                    usersRepository.getUser(uid)
                        .collect { user ->
                            println(uid)
                            println(user)
                            _mainUiState.update { it.copy(currentUser = user) }
                            Log.d(
                                TAG,
                                "Is admin? ${user.admin}"
                            )
                        }
                }
            }

        }
    }

    private fun fetchStartDateFromConfig() {
        Firebase.remoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                Log.d(
                    TAG,
                    "Remote config updated"
                )
                val value = Firebase.remoteConfig.getLong("period_start_day")
                    .toInt()
                _periodState.update {
                    it.copy(
                        currentPeriod = Period.getMonthlyPeriod(startDayInclusive = value)
                    )
                }
                Log.d(
                    TAG,
                    "New value: $value"
                )

            }
    }


    fun resetCurrentUser() {
        _mainUiState.update {
            it.copy(currentUser = User())
        }
    }

    fun fetchDataAfterLogin() {
        getCurrentUserDetails()
        getUsersFromDatabase()
    }


    private fun addConfigUpdateListener() {

        Firebase.remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d(
                    TAG,
                    "Updated keys: " + configUpdate.updatedKeys
                )

                if (configUpdate.updatedKeys.contains("period_start_day")) {
                    Firebase.remoteConfig.activate()
                        .addOnCompleteListener {
                            Log.d(
                                TAG,
                                "Remote config updated"
                            )
                            val value = getStartDate()
                            _periodState.update {
                                it.copy(
                                    currentPeriod = Period.getMonthlyPeriod(startDayInclusive = value)
                                )
                            }
                            Log.d(
                                TAG,
                                "New value: $value"
                            )

                        }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(
                    TAG,
                    "Config update error with code: " + error.code,
                    error
                )
            }
        })

    }

    private fun getStartDate(): Int {
        val value = Firebase.remoteConfig.getLong("period_start_day")
            .toInt()

        if (value in 1..31) {
            return value
        }
        else {
            Log.e(
                TAG,
                "Invalid value for period_start_day: $value"
            )
            fetchStartDateFromConfig()
        }

        return 15
    }

    fun resetPeriodState() {
        _periodState.update {
            it.copy(userSelectedPeriod = it.currentPeriod)
        }
    }

    fun getPeriods(): List<Period> {
        val currentPeriod = periodState.value.currentPeriod

        val nextPeriodsCount = 1
        val previousPeriodsCount = 6
        val totalPeriodsCount = nextPeriodsCount + previousPeriodsCount + 1

        val periods = (0 until totalPeriodsCount).map {
            currentPeriod.previousMonthly(previousPeriodsCount - it)
        }

        return periods
    }
}