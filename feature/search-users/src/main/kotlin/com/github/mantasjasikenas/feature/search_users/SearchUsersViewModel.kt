package com.github.mantasjasikenas.feature.search_users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchUsersViewModel @Inject constructor(
    usersRepository: UsersRepository
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(true)
    val isSearching = _isSearching.asStateFlow()

    private val _selectedUsers = MutableStateFlow(emptySet<User>())
    val selectedUsers = _selectedUsers.asStateFlow()

    private val _allUsers = usersRepository
        .getUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredUsers = searchText
//        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .combine(_allUsers) { text, users ->
            if (text.isBlank()) {
                users
            } else {
                users.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _allUsers.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onSelectedUserUpdate(user: User, isSelected: Boolean) {
        _selectedUsers.update { selectedUsers ->
            if (isSelected) {
                selectedUsers + user
            } else {
                selectedUsers - user
            }
        }
    }

}