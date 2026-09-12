package com.sandeep.aijobapplicationtracker.presentation.screens.addinterview

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
import com.sandeep.aijobapplicationtracker.domain.repository.AiAnalyzerRepository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class AddInterviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val repository: com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository,
    private val aiAnalyzer: AiAnalyzerRepository
) : ViewModel() {

    private val jobId: String = savedStateHandle.get<String>("jobId") ?: ""

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    private val _companyName = MutableStateFlow("")
    val companyName: StateFlow<String> = _companyName

    private val _jobTitle = MutableStateFlow("")
    val jobTitle: StateFlow<String> = _jobTitle

    init {
        viewModelScope.launch {
            try {
                val apps = repository.getApplications().first()
                val app = apps.find { it.id == jobId }
                if (app != null) {
                    _companyName.value = app.company
                    _jobTitle.value = app.jobTitle
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    private val _generatedSummary = MutableStateFlow("")
    val generatedSummary: StateFlow<String> = _generatedSummary

    private val _isGeneratingSummary = MutableStateFlow(false)
    val isGeneratingSummary: StateFlow<Boolean> = _isGeneratingSummary

    fun generateAiSummary(type: String) {
        viewModelScope.launch {
            _isGeneratingSummary.value = true
            try {
                val apps = repository.getApplications().first()
                val app = apps.find { it.id == jobId }
                val jd = app?.jobDescription ?: ""
                val prompt = "Generate a short bullet-point prep summary for a $type interview for the role of ${app?.jobTitle ?: "the candidate"}. Job description context: $jd"
                
                // Using analyzeJobDescription temporarily as a generic text generator, or we can use the generative model directly.
                // Wait, it's better to add a generic `generateText` in AiAnalyzerRepository or just use the model here.
                // Let's just create a prompt and use the repository's generateInterviewPlan but just extract the tip.
                val plan = aiAnalyzer.generateInterviewPlan(jd, app?.jobTitle ?: "", app?.company ?: "")
                if (plan.isSuccess) {
                    val p = plan.getOrNull()
                    val summary = "AI Interview Prep Summary:\n" +
                        "• Strategy: ${p?.strategyTip}\n" +
                        "• Focus Areas: ${p?.focusAreas?.joinToString(", ") { it.topic }}\n"
                    _generatedSummary.value = summary
                } else {
                    _generatedSummary.value = "AI Generation Failed."
                }
            } catch (e: Exception) {
                _generatedSummary.value = "AI Generation Failed: ${e.message}"
            } finally {
                _isGeneratingSummary.value = false
            }
        }
    }

    fun saveInterview(
        roundNumber: String,
        type: String,
        dateTime: String,
        meetingUrl: String,
        interviewer: String,
        notes: String,
        remindMe: Boolean,
        reminderTime: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val apps = repository.getApplications().first()
                val app = apps.find { it.id == jobId }
                if (app != null) {
                    val newInterview = com.sandeep.aijobapplicationtracker.domain.model.InterviewModel(
                        roundNumber = roundNumber,
                        type = type,
                        dateTime = dateTime,
                        meetingUrl = meetingUrl,
                        interviewer = interviewer,
                        notes = notes
                    )
                    
                    val updatedInterviews = app.interviews.toMutableList()
                    updatedInterviews.add(newInterview)
                    
                    val updatedApp = app.copy(interviews = updatedInterviews)
                    repository.updateApplication(updatedApp)

                    if (remindMe) {
                        try {
                            val inputData = androidx.work.Data.Builder()
                                .putString("title", "Interview Reminder")
                                .putString("message", "Upcoming $type interview for ${app.company}!")
                                .build()
                            
                            val delayMinutes = when(reminderTime) {
                                "15 minutes before" -> 15L
                                "1 hour before" -> 60L
                                "1 day before" -> 1440L
                                "2 days before" -> 2880L
                                else -> 15L
                            }

                            // dateTime format: "2026-10-15 02:30 PM"
                            try {
                                val format = java.text.SimpleDateFormat("yyyy-MM-dd hh:mm a", java.util.Locale.getDefault())
                                val interviewDate = format.parse(dateTime)
                                if (interviewDate != null) {
                                    val reminderTimeMillis = interviewDate.time - (delayMinutes * 60 * 1000)
                                    val initialDelayMillis = reminderTimeMillis - System.currentTimeMillis()

                                    if (initialDelayMillis > 0) {
                                        val workRequest = androidx.work.OneTimeWorkRequestBuilder<com.sandeep.aijobapplicationtracker.utils.ReminderWorker>()
                                            .setInitialDelay(initialDelayMillis, java.util.concurrent.TimeUnit.MILLISECONDS)
                                            .setInputData(inputData)
                                            .build()
                                        
                                        androidx.work.WorkManager.getInstance(context).enqueue(workRequest)
                                    }
                                }
                            } catch (e: Exception) {
                                // Fallback: just enqueue it right away if parsing fails, or log it
                            }
                        } catch (e: Exception) {
                            // ignore
                        }
                    }
                    
                    _uiState.value = UiState.Success(Unit)
                } else {
                    _uiState.value = UiState.Error("Application not found.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}
