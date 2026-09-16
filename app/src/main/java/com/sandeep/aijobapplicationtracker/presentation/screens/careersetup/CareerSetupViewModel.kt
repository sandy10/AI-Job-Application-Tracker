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

import com.sandeep.aijobapplicationtracker.domain.repository.ProfileRepository
import com.sandeep.aijobapplicationtracker.domain.model.UserProfileModel

@HiltViewModel
class CareerSetupViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    // Expose the profile flow directly to the UI
    val userProfile = profileRepository.getProfile()

    fun saveProfile(
        name: String,
        experienceLevel: String,
        yearsOfExperience: String,
        primaryRole: String,
        skills: List<String>,
        location: String,
        currentCtc: String,
        expectedCtc: String,
        noticePeriod: String,
        workPreference: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            // Basic validation
            if (name.isBlank() || experienceLevel.isBlank() || yearsOfExperience.isBlank() ||
                primaryRole.isBlank() || location.isBlank()) {
                _uiState.value = UiState.Error("Please fill in all required fields")
                delay(2000)
                _uiState.value = UiState.Idle
                return@launch
            }
            
            // Save to Firestore repository
            val profile = UserProfileModel(
                name = name,
                targetRole = primaryRole,
                experienceLevel = experienceLevel,
                yearsExperience = yearsOfExperience,
                location = location,
                workPreference = workPreference,
                currentCtc = currentCtc,
                expectedCtc = expectedCtc,
                noticePeriod = noticePeriod,
                skills = skills
            )
            
            val result = profileRepository.saveProfile(profile)
            
            if (result.isSuccess) {
                delay(1000)
                _uiState.value = UiState.Success(Unit)
            } else {
                _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Failed to save profile")
                delay(3000)
                _uiState.value = UiState.Idle
            }
        }
    }
}
