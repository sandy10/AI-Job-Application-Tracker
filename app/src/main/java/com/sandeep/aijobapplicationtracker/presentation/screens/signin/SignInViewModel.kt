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
import com.sandeep.aijobapplicationtracker.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.first

/**
 * ViewModel for the Sign In screen.
 * Handles email/password login and Google Sign-In via Credential Manager.
 */
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val uiState: StateFlow<UiState<Boolean>> = _uiState

    /**
     * Signs in with email and password using Firebase Auth.
     */
    fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            val result = authRepository.login(email, pass)
            
            if (result.isSuccess) {
                val profile = profileRepository.getProfile().first()
                val isComplete = profile?.targetRole?.isNotBlank() == true
                _uiState.value = UiState.Success(isComplete)
            } else {
                _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Sign in failed")
                // Reset to idle after error so the user can try again
                delay(2000)
                _uiState.value = UiState.Idle
            }
        }
    }

    /**
     * Signs up with email and password using Firebase Auth.
     */
    fun signUp(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            val result = authRepository.signUp(email, pass)
            
            if (result.isSuccess) {
                val profile = profileRepository.getProfile().first()
                val isComplete = profile?.targetRole?.isNotBlank() == true
                _uiState.value = UiState.Success(isComplete)
            } else {
                _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Sign up failed")
                delay(2000)
                _uiState.value = UiState.Idle
            }
        }
    }

    /**
     * Sends a password reset email.
     */
    fun resetPassword(email: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.sendPasswordResetEmail(email)
            if (result.isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Failed to send reset email")
            }
        }
    }

    /**
     * Called by the UI after the Credential Manager returns a Google ID token.
     * Exchanges the token for a Firebase credential and signs in.
     */
    fun signInWithGoogleIdToken(idToken: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val result = authRepository.signInWithGoogleIdToken(idToken)

            if (result.isSuccess) {
                val profile = profileRepository.getProfile().first()
                val isComplete = profile?.targetRole?.isNotBlank() == true
                _uiState.value = UiState.Success(isComplete)
            } else {
                _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Google Sign In failed")
                delay(2000)
                _uiState.value = UiState.Idle
            }
        }
    }

    /**
     * Called when Google Sign-In fails at the UI layer (e.g. user cancelled).
     */
    fun onGoogleSignInFailed(errorMessage: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Error(errorMessage)
            delay(2000)
            _uiState.value = UiState.Idle
        }
    }

    /**
     * Sets loading state when Google Sign-In is initiated.
     */
    fun setLoading() {
        _uiState.value = UiState.Loading
    }
}
