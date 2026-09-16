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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.sandeep.aijobapplicationtracker.domain.repository.AiAnalyzerRepository
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

data class AddInterviewFormState(
    val date: String = "",
    val time: String = "",
    val meetingUrl: String = "",
    val interviewer: String = "",
    val notes: String = "",
    val remindMe: Boolean = true,
    val interviewRound: String = "1st Round",
    val interviewType: String = "Technical",
    val reminderTime: String = "1 day before"
)

sealed class AddInterviewEvent {
    data class DateChanged(val date: String) : AddInterviewEvent()
    data class TimeChanged(val time: String) : AddInterviewEvent()
    data class MeetingUrlChanged(val url: String) : AddInterviewEvent()
    data class InterviewerChanged(val interviewer: String) : AddInterviewEvent()
    data class NotesChanged(val notes: String) : AddInterviewEvent()
    data class RemindMeChanged(val remindMe: Boolean) : AddInterviewEvent()
    data class InterviewRoundChanged(val round: String) : AddInterviewEvent()
    data class InterviewTypeChanged(val type: String) : AddInterviewEvent()
    data class ReminderTimeChanged(val time: String) : AddInterviewEvent()
    object SaveClicked : AddInterviewEvent()
    object ClearError : AddInterviewEvent()
    object GenerateAiSummaryClicked : AddInterviewEvent()
}

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

    private val _formState = MutableStateFlow(AddInterviewFormState())
    val formState: StateFlow<AddInterviewFormState> = _formState

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

    fun onEvent(event: AddInterviewEvent) {
        when (event) {
            is AddInterviewEvent.DateChanged -> _formState.update { it.copy(date = event.date) }
            is AddInterviewEvent.TimeChanged -> _formState.update { it.copy(time = event.time) }
            is AddInterviewEvent.MeetingUrlChanged -> _formState.update { it.copy(meetingUrl = event.url) }
            is AddInterviewEvent.InterviewerChanged -> _formState.update { it.copy(interviewer = event.interviewer) }
            is AddInterviewEvent.NotesChanged -> _formState.update { it.copy(notes = event.notes) }
            is AddInterviewEvent.RemindMeChanged -> _formState.update { it.copy(remindMe = event.remindMe) }
            is AddInterviewEvent.InterviewRoundChanged -> _formState.update { it.copy(interviewRound = event.round) }
            is AddInterviewEvent.InterviewTypeChanged -> _formState.update { it.copy(interviewType = event.type) }
            is AddInterviewEvent.ReminderTimeChanged -> _formState.update { it.copy(reminderTime = event.time) }
            is AddInterviewEvent.SaveClicked -> saveInterview()
            is AddInterviewEvent.ClearError -> _uiState.value = UiState.Idle
            is AddInterviewEvent.GenerateAiSummaryClicked -> generateAiSummary(_formState.value.interviewType)
        }
    }

    private fun generateAiSummary(type: String) {
        viewModelScope.launch {
            _isGeneratingSummary.value = true
            try {
                val apps = repository.getApplications().first()
                val app = apps.find { it.id == jobId }
                val jd = app?.jobDescription ?: ""
                
                val plan = aiAnalyzer.generateInterviewPlan(jd, app?.jobTitle ?: "", app?.company ?: "")
                if (plan.isSuccess) {
                    val p = plan.getOrNull()
                    val summary = "AI Interview Prep Summary:\n" +
                        "• Strategy: ${p?.strategyTip}\n" +
                        "• Focus Areas: ${p?.focusAreas?.joinToString(", ") { it.topic }}\n"
                    _generatedSummary.value = summary
                    _formState.update { it.copy(notes = summary) }
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

    private fun saveInterview() {
        val state = _formState.value
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val apps = repository.getApplications().first()
                val app = apps.find { it.id == jobId }
                if (app != null) {
                    val newInterview = com.sandeep.aijobapplicationtracker.domain.model.InterviewModel(
                        roundNumber = state.interviewRound,
                        type = state.interviewType,
                        dateTime = "${state.date} ${state.time}",
                        meetingUrl = state.meetingUrl,
                        interviewer = state.interviewer,
                        notes = state.notes
                    )
                    
                    val updatedInterviews = app.interviews.toMutableList()
                    updatedInterviews.add(newInterview)
                    
                    val updatedApp = app.copy(interviews = updatedInterviews)
                    repository.updateApplication(updatedApp)

                    if (state.remindMe) {
                        try {
                            val inputData = androidx.work.Data.Builder()
                                .putString("title", "Interview Reminder")
                                .putString("message", "Upcoming ${state.interviewType} interview for ${app.company}!")
                                .build()
                            
                            val delayMinutes = when(state.reminderTime) {
                                "15 minutes before" -> 15L
                                "1 hour before" -> 60L
                                "1 day before" -> 1440L
                                "2 days before" -> 2880L
                                else -> 15L
                            }

                            // dateTime format: "2026-10-15 02:30 PM"
                            try {
                                val format = java.text.SimpleDateFormat("yyyy-MM-dd hh:mm a", java.util.Locale.getDefault())
                                val interviewDate = format.parse("${state.date} ${state.time}")
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
