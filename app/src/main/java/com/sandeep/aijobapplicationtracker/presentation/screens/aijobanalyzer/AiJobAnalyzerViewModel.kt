package com.sandeep.aijobapplicationtracker.presentation.screens.aijobanalyzer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
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
    private val profileRepository: com.sandeep.aijobapplicationtracker.domain.repository.ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    fun analyzeJobDescription(jobDescription: String) {
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
