package com.sandeep.aijobapplicationtracker.presentation.screens.careersetup

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
class CareerSetupViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    fun saveProfile(
        name: String,
        experienceLevel: String,
        yearsOfExperience: String,
        primaryRole: String,
        skills: String,
        location: String,
        currentCtc: String,
        expectedCtc: String,
        noticePeriod: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            // Basic validation
            if (name.isBlank() || experienceLevel.isBlank() || yearsOfExperience.isBlank() ||
                primaryRole.isBlank() || skills.isBlank() || location.isBlank()) {
                _uiState.value = UiState.Error("Please fill in all required fields")
                delay(2000)
                _uiState.value = UiState.Idle
                return@launch
            }
            
            // Simulate network request to save to Firestore
            // TODO: Implement actual Firestore saving here
            delay(1500)
            
            _uiState.value = UiState.Success(Unit)
        }
    }
}
