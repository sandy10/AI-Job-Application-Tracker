package com.sandeep.aijobapplicationtracker.presentation.screens.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            // Simulate network request
            // TODO: Implement actual Firebase Auth here
            delay(1500)
            
            if (email.isNotBlank() && pass.isNotBlank()) {
                _uiState.value = UiState.Success(Unit)
            } else {
                _uiState.value = UiState.Error("Email and password cannot be empty")
                // Reset to idle after error to allow user to try again
                delay(2000)
                _uiState.value = UiState.Idle
            }
        }
    }
    
    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // TODO: Implement actual Google Sign In here
            delay(1500)
            _uiState.value = UiState.Success(Unit)
        }
    }
}
