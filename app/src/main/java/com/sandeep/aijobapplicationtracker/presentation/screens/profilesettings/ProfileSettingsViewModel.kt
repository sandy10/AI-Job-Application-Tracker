package com.sandeep.aijobapplicationtracker.presentation.screens.profilesettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.domain.model.UserProfileModel
import com.sandeep.aijobapplicationtracker.domain.repository.AuthRepository
import com.sandeep.aijobapplicationtracker.domain.repository.ProfileRepository
import com.sandeep.aijobapplicationtracker.utils.AnalyticsHelper
import com.sandeep.aijobapplicationtracker.utils.Constants
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

import com.sandeep.aijobapplicationtracker.utils.CrashlyticsHelper

import com.sandeep.aijobapplicationtracker.domain.repository.AiAnalyzerRepository

/**
 * ViewModel for the Profile Settings screen.
 * Loads real user profile data from Firestore via [ProfileRepository].
 */
@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val aiAnalyzerRepository: AiAnalyzerRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val crashlyticsHelper: CrashlyticsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<UserProfileModel>>(UiState.Loading)
    val uiState: StateFlow<UiState<UserProfileModel>> = _uiState

    init {
        loadProfile()
    }

    /**
     * Subscribes to the Firestore profile flow so the UI
     * always reflects the latest saved profile data.
     */
    private fun loadProfile() {
        viewModelScope.launch {
            profileRepository.getProfile().collect { profile ->
                if (profile != null && profile.name.isNotBlank()) {
                    _uiState.value = UiState.Success(profile)
                } else {
                    Timber.w("Profile is null or empty")
                    _uiState.value = UiState.Success(
                        UserProfileModel(
                            name = authRepository.getCurrentUserName() ?: "User",
                            targetRole = "",
                            experienceLevel = "",
                            yearsExperience = "",
                            location = "",
                            workPreference = ""
                        )
                    )
                }
            }
        }
    }

    /**
     * Signs the user out and emits [UiState.Empty] to trigger
     * navigation back to the Sign In screen.
     */
    fun logout() {
        analyticsHelper.trackLogout()
        crashlyticsHelper.clearUserId()
        viewModelScope.launch {
            // H5 Fix: Clear AI singleton state to prevent leaking across sessions
            aiAnalyzerRepository.clearExtractedData()
            authRepository.logout()
            _uiState.value = UiState.Empty
        }
    }
}
