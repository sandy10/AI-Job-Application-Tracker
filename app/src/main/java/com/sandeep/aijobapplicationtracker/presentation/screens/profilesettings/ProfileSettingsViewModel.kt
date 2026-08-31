package com.sandeep.aijobapplicationtracker.presentation.screens.profilesettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.sandeep.aijobapplicationtracker.domain.repository.AuthRepository

data class UserProfile(
    val name: String,
    val role: String,
    val email: String
)

@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<UserProfile>>(UiState.Loading)
    val uiState: StateFlow<UiState<UserProfile>> = _uiState

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(1000)
            
            _uiState.value = UiState.Success(
                UserProfile(
                    name = "Sandeep",
                    role = "Senior Android Developer",
                    email = "sandeep@example.com"
                )
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = UiState.Empty // using empty state to trigger navigation to SignIn
        }
    }
}
