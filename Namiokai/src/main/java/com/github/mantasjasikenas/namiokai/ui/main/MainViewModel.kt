package com.github.mantasjasikenas.namiokai.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.namiokai.data.UsersRepository
import com.github.mantasjasikenas.namiokai.data.repository.preferences.PreferencesRepository
import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.model.getMonthlyPeriod
import com.github.mantasjasikenas.namiokai.model.previousMonthly
import com.github.mantasjasikenas.namiokai.model.theme.ThemePreferences
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
    private val usersRepository: UsersRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _mainUiState = MutableStateFlow(MainUiState(currentUser = getUserFromAuth()))
    val mainUiState = _mainUiState.asStateFlow()

    private val _periodState = MutableStateFlow(PeriodUiState())
    val periodState = _periodState.asStateFlow()

    init {
        getThemePreferences()
        getUsersDetails()
        initPeriodState()
        addConfigUpdateListener()
    }

    private fun initPeriodState() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _periodState.update {
                    val period = Period.getMonthlyPeriod(getStartDate())
                    val periods = generatePeriods(period)
                    it.copy(
                        currentPeriod = period,
                        userSelectedPeriod = period,
                        periods = periods
                    )
                }
            }
        }
    }

    private fun generatePeriods(currentPeriod: Period): List<Period> {
        //val currentPeriod = periodState.value.currentPeriod
        val nextPeriodsCount = 0
        val previousPeriodsCount = 3
        val totalPeriodsCount = nextPeriodsCount + previousPeriodsCount + 1

        val periods = (0 until totalPeriodsCount).map {
            currentPeriod.previousMonthly(previousPeriodsCount - it)
        }

        return periods
    }

    private fun getThemePreferences() {
        viewModelScope.launch {
            preferencesRepository.themePreferences.collect { themePreferences ->
                _mainUiState.update {
                    it.copy(
                        themePreferences = themePreferences,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateThemePreferences(themePreferences: ThemePreferences) {
        viewModelScope.launch {
            preferencesRepository.updateThemePreferences(themePreferences)
        }
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

    private fun getUsersDetails() {
        if (Firebase.auth.currentUser == null) {
            return
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                usersRepository.getUsers()
                    .collect { users ->
                        val usersMap = users.associateBy { it.uid }
                        val currentUser = usersMap[Firebase.auth.currentUser!!.uid]

                        _mainUiState.update {
                            it.copy(
                                usersMap = usersMap,
                                currentUser = currentUser ?: User()
                            )
                        }
                    }
            }
        }
    }


    private fun fetchStartDateFromConfig() {
        Firebase.remoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                val value = Firebase.remoteConfig.getLong("period_start_day")
                    .toInt()
                _periodState.update {
                    it.copy(
                        currentPeriod = Period.getMonthlyPeriod(startDayInclusive = value)
                    )
                }
            }
    }


    fun resetCurrentUser() {
        _mainUiState.update {
            it.copy(currentUser = User())
        }
    }

    fun fetchDataAfterLogin() {
        getUsersDetails()
    }


    private fun addConfigUpdateListener() {

        Firebase.remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                if (configUpdate.updatedKeys.contains("period_start_day")) {
                    Firebase.remoteConfig.activate()
                        .addOnCompleteListener {
                            val value = getStartDate()
                            _periodState.update {
                                it.copy(
                                    currentPeriod = Period.getMonthlyPeriod(startDayInclusive = value)
                                )
                            }
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

    private fun getUserFromAuth() = Firebase.auth.currentUser?.toUser() ?: User()
}