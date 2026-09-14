package com.sandeep.aijobapplicationtracker.presentation.screens.airesumematch

import androidx.lifecycle.SavedStateHandle
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

data class ResumeMatchResult(
    val company: String,
    val jobTitle: String,
    val score: Int,
    val scoreLabel: String,
    val matchedSkills: List<String>,
    val missingSkills: List<String>,
    val experienceDetails: String,
    val recommendation: String
)

@HiltViewModel
class AiResumeMatchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository,
    private val profileRepository: com.sandeep.aijobapplicationtracker.domain.repository.ProfileRepository,
    private val aiAnalyzer: com.sandeep.aijobapplicationtracker.domain.repository.AiAnalyzerRepository,
    private val networkMonitor: com.sandeep.aijobapplicationtracker.utils.NetworkMonitor
) : ViewModel() {

    val jobId: String = savedStateHandle.get<String>("jobId") ?: ""

    private val _uiState = MutableStateFlow<UiState<ResumeMatchResult>>(UiState.Loading)
    val uiState: StateFlow<UiState<ResumeMatchResult>> = _uiState

    init {
        analyzeMatch()
    }

    private fun analyzeMatch() {
        if (!networkMonitor.isOnline()) {
            _uiState.value = UiState.Error("No internet connection")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val apps = repository.getApplications().first()
                val app = apps.find { it.id == jobId }
                
                if (app != null) {
                    val profile = profileRepository.getProfile().first()
                    val candidateProfile = if (profile != null) {
                        "Name: ${profile.name}, Role: ${profile.targetRole}, Experience: ${profile.experienceLevel} (${profile.yearsExperience} years), Skills: ${profile.skills.joinToString(", ")}"
                    } else {
                        "No profile available"
                    }
                    
                    val result = aiAnalyzer.generateResumeMatchAnalysis(
                        jobDescription = app.jobDescription.ifBlank { "Role: ${app.jobTitle} at ${app.company}" },
                        candidateProfile = candidateProfile
                    )
                    
                    if (result.isSuccess) {
                        val matchResult = result.getOrNull()
                        if (matchResult != null) {
                            val scoreLabel = when {
                                app.matchScore >= 80 -> "Strong Match"
                                app.matchScore >= 60 -> "Good Match"
                                app.matchScore >= 40 -> "Fair Match"
                                else -> "Weak Match"
                            }
                            
                            // Always use the original match score that was generated when JD was added
                            val finalResult = matchResult.copy(
                                company = app.company, 
                                jobTitle = app.jobTitle,
                                score = app.matchScore,
                                scoreLabel = scoreLabel
                            )
                            _uiState.value = UiState.Success(finalResult)
                        } else {
                            _uiState.value = UiState.Error("Analysis failed to return a result.")
                        }
                    } else {
                        _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                    }
                } else {
                    _uiState.value = UiState.Error("Application not found.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
