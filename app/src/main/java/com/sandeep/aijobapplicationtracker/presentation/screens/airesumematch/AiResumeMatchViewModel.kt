package com.sandeep.aijobapplicationtracker.presentation.screens.airesumematch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResumeMatchResult(
    val score: Int,
    val scoreLabel: String,
    val matchedSkills: List<String>,
    val missingSkills: List<String>,
    val experienceDetails: String,
    val recommendation: String
)

@HiltViewModel
class AiResumeMatchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val jobId: String = savedStateHandle.get<String>("jobId") ?: ""

    private val _uiState = MutableStateFlow<UiState<ResumeMatchResult>>(UiState.Loading)
    val uiState: StateFlow<UiState<ResumeMatchResult>> = _uiState

    init {
        analyzeMatch()
    }

    private fun analyzeMatch() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // Simulate Gemini AI evaluation of primary resume vs JD
            delay(2000)
            
            _uiState.value = UiState.Success(
                ResumeMatchResult(
                    score = 82,
                    scoreLabel = "Strong Match",
                    matchedSkills = listOf("Kotlin", "Jetpack Compose", "MVVM", "Firebase", "Coroutines"),
                    missingSkills = listOf("KMP", "GraphQL", "CI/CD"),
                    experienceDetails = "9 years — Required: 7+",
                    recommendation = "Strong candidate. Highlight your Compose architecture and KMP experience more prominently."
                )
            )
        }
    }
}
