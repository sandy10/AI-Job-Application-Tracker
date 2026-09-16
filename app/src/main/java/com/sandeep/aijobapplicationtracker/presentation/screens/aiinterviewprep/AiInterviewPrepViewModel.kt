package com.sandeep.aijobapplicationtracker.presentation.screens.aiinterviewprep

import androidx.lifecycle.SavedStateHandle
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
import com.sandeep.aijobapplicationtracker.domain.model.InterviewPlan
import com.sandeep.aijobapplicationtracker.domain.model.FocusArea
import com.sandeep.aijobapplicationtracker.domain.model.QuestionAnswer


@HiltViewModel
class AiInterviewPrepViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val jobRepository: com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository,
    private val aiRepository: com.sandeep.aijobapplicationtracker.domain.repository.AiAnalyzerRepository,
    private val networkMonitor: com.sandeep.aijobapplicationtracker.utils.NetworkMonitor,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {

    private val jobId: String = savedStateHandle.get<String>("jobId") ?: ""

    private val _uiState = MutableStateFlow<UiState<InterviewPlan>>(UiState.Loading)
    val uiState: StateFlow<UiState<InterviewPlan>> = _uiState

    init {
        generatePrepPlan()
    }

    private val _isGeneratingMore = MutableStateFlow(false)
    val isGeneratingMore: StateFlow<Boolean> = _isGeneratingMore

    fun generatePrepPlan(forceRegenerate: Boolean = false) {
        analyticsHelper.trackAiFeatureUsed(Constants.Analytics.FEATURE_INTERVIEW_PREP, jobId)
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val apps = jobRepository.getApplications().first()
                val app = apps.find { it.id == jobId }
                
                if (app != null) {
                    if (!forceRegenerate && app.aiInterviewPlanJson.isNotBlank()) {
                        val cachedPlan = InterviewPlan.fromJsonString(app.aiInterviewPlanJson)
                        if (cachedPlan != null) {
                            _uiState.value = UiState.Success(cachedPlan)
                            return@launch
                        }
                    }

                    if (!networkMonitor.isOnline()) {
                        _uiState.value = UiState.Error("No internet connection")
                        return@launch
                    }

                    val result = aiRepository.generateInterviewPlan(
                        jobDescription = app.jobDescription,
                        role = app.jobTitle,
                        company = app.company
                    )
                    
                    if (result.isSuccess) {
                        val plan = result.getOrNull()!!
                        _uiState.value = UiState.Success(plan)
                        
                        // Save back to repository
                        val updatedApp = app.copy(aiInterviewPlanJson = plan.toJsonString())
                        jobRepository.saveApplication(updatedApp)
                    } else {
                        _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Failed to generate plan.")
                    }
                } else {
                    _uiState.value = UiState.Error("Application not found.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun generateMoreQuestions() {
        val currentState = _uiState.value
        if (currentState is UiState.Success && !_isGeneratingMore.value) {
            viewModelScope.launch {
                val currentPlan = currentState.data
                _isGeneratingMore.value = true
                
                try {
                    val result = aiRepository.generateMoreQuestions(
                        role = currentPlan.role,
                        existingQuestions = currentPlan.likelyQuestions.map { it.question }
                    )
                    
                    if (result.isSuccess) {
                        val newQuestions = currentPlan.likelyQuestions.toMutableList()
                        newQuestions.addAll(result.getOrNull() ?: emptyList())
                        _uiState.value = UiState.Success(currentPlan.copy(likelyQuestions = newQuestions))
                    }
                } catch (e: Exception) {
                    // Ignore or handle
                } finally {
                    _isGeneratingMore.value = false
                }
            }
        }
    }
}
