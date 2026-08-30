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

/**
 * ViewModel for Splash Screen.
 */
@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    init {
        startSplashDelay()
    }

    private fun startSplashDelay() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // Simulate splash delay or initialization checks (e.g., Auth check)
            // TODO: Add real auth checking here in the future
            delay(1500)
            _uiState.value = UiState.Success(Unit)
        }
    }
}
