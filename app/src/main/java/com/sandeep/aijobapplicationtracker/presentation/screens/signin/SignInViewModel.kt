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

import com.sandeep.aijobapplicationtracker.domain.repository.AuthRepository

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            val result = authRepository.login(email, pass)
            
            if (result.isSuccess) {
                _uiState.value = UiState.Success(Unit)
            } else {
                _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Sign in failed")
                // Reset to idle after error to allow user to try again
                delay(2000)
                _uiState.value = UiState.Idle
            }
        }
    }
    
    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            val result = authRepository.loginWithGoogle()
            
            if (result.isSuccess) {
                _uiState.value = UiState.Success(Unit)
            } else {
                _uiState.value = UiState.Error("Google Sign In failed")
                delay(2000)
                _uiState.value = UiState.Idle
            }
        }
    }
}
