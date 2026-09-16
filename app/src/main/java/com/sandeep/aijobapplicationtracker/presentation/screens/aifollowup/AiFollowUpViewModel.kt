package com.sandeep.aijobapplicationtracker.presentation.screens.aifollowup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.domain.repository.AiAnalyzerRepository
import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository
import com.sandeep.aijobapplicationtracker.utils.AnalyticsHelper
import com.sandeep.aijobapplicationtracker.utils.Constants
import com.sandeep.aijobapplicationtracker.utils.NetworkMonitor
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.concurrent.TimeUnit
import timber.log.Timber

@HiltViewModel
class AiFollowUpViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val jobRepository: JobApplicationRepository,
    private val aiRepository: AiAnalyzerRepository,
    private val networkMonitor: NetworkMonitor,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {

    private val jobId: String = checkNotNull(savedStateHandle["jobId"])

    private val _uiState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val uiState: StateFlow<UiState<String>> = _uiState

    init {
        generateDraft("follow_up")
    }

    fun generateDraft(type: String = "follow_up") {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                if (!networkMonitor.isOnline()) {
                    _uiState.value = UiState.Error("No internet connection")
                    return@launch
                }

                val apps = jobRepository.getApplications().first()
                val app = apps.find { it.id == jobId }
                
                if (app != null) {
                    var daysSinceApplied = 7 // default
                    if (app.dateApplied.isNotBlank()) {
                        try {
                            val format = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                            val date = format.parse(app.dateApplied)
                            if (date != null) {
                                val diff = System.currentTimeMillis() - date.time
                                daysSinceApplied = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diff).toInt()
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Error parsing dateApplied")
                        }
                    }
                    
                    val profile = com.sandeep.aijobapplicationtracker.domain.model.UserProfileModel(
                        name = "User", 
                        targetRole = app.jobTitle, 
                        experienceLevel = "Mid",
                        yearsExperience = "3",
                        location = "Remote",
                        workPreference = "Remote"
                    )
                    val drafts = jobRepository.getDraftsForJob(jobId).first()
                    
                    val result = if (type == "cover_letter") {
                        aiRepository.generateCoverLetter(app, profile, drafts)
                    } else {
                        aiRepository.generateFollowUpEmail(app.company, app.jobTitle, app.recruiter.takeIf { it.isNotBlank() }, daysSinceApplied)
                    }
                    
                    if (result.isSuccess) {
                        jobRepository.saveDraft(
                            com.sandeep.aijobapplicationtracker.domain.model.DraftModel(
                                id = java.util.UUID.randomUUID().toString(),
                                jobId = jobId,
                                type = type,
                                contents = result.getOrNull()!!,
                                status = "draft"
                            )
                        )
                        _uiState.value = UiState.Success(result.getOrNull()!!)
                    } else {
                        _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Failed to generate draft")
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
