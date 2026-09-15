package com.sandeep.aijobapplicationtracker.presentation.screens.aijobanalyzer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
import com.sandeep.aijobapplicationtracker.utils.AnalyticsHelper
import com.sandeep.aijobapplicationtracker.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.sandeep.aijobapplicationtracker.domain.repository.AiAnalyzerRepository

@HiltViewModel
class AiJobAnalyzerViewModel @Inject constructor(
    private val repository: AiAnalyzerRepository,
    private val profileRepository: com.sandeep.aijobapplicationtracker.domain.repository.ProfileRepository,
    private val networkMonitor: com.sandeep.aijobapplicationtracker.utils.NetworkMonitor,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    fun analyzeJobDescription(jobDescription: String) {
        analyticsHelper.trackAiFeatureUsed(Constants.Analytics.FEATURE_JOB_ANALYZER)
        if (!networkMonitor.isOnline()) {
            _uiState.value = UiState.Error("No internet connection")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            val profile = profileRepository.getProfile().first()
            val profileContext = profile?.let {
                "Role: ${it.targetRole}\nExperience: ${it.yearsExperience}\nSkills: ${it.skills.joinToString(", ")}"
            }

            val result = repository.analyzeJobDescription(jobDescription, profileContext)
            if (result.isSuccess) {
                _uiState.value = UiState.Success(Unit)
            } else {
                _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Analysis failed")
            }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
