package com.sandeep.aijobapplicationtracker.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import com.sandeep.aijobapplicationtracker.domain.repository.AuthRepository

import com.sandeep.aijobapplicationtracker.domain.repository.ProfileRepository

/**
 * ViewModel for Splash Screen.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    // Pair of <IsLoggedIn, IsProfileComplete>
    private val _uiState = MutableStateFlow<UiState<Pair<Boolean, Boolean>>>(UiState.Idle)
    val uiState: StateFlow<UiState<Pair<Boolean, Boolean>>> = _uiState

    init {
        startSplashDelay()
    }

    private fun startSplashDelay() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            // Wait for 1.5s for splash effect
            delay(1500)
            
            // Check if auth session is truly valid (handles deleted users)
            val isLoggedIn = authRepository.verifySession()
            
            if (isLoggedIn) {
                // If logged in, check if profile has been completed (i.e. targetRole is not blank)
                val profile = profileRepository.getProfile().first()
                val isComplete = profile?.targetRole?.isNotBlank() == true
                _uiState.value = UiState.Success(Pair(isLoggedIn, isComplete))
            } else {
                _uiState.value = UiState.Success(Pair(false, false))
            }
        }
    }
}
