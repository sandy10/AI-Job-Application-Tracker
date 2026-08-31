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

/**
 * ViewModel for Splash Screen.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val uiState: StateFlow<UiState<Boolean>> = _uiState

    init {
        startSplashDelay()
    }

    private fun startSplashDelay() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            // Wait for 1.5s for splash effect
            delay(1500)
            
            // Check auth state
            val isLoggedIn = authRepository.isLoggedIn().first()
            _uiState.value = UiState.Success(isLoggedIn)
        }
    }
}
