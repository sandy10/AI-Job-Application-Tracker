package com.sandeep.aijobapplicationtracker.presentation.screens.applicationdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.presentation.screens.applications.ApplicationStatus
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository

data class NextAction(
    val title: String,
    val description: String,
    val isAiAction: Boolean = false
)

data class InterviewRound(
    val title: String,
    val date: String,
    val time: String,
    val type: String
)

data class JobDetailData(
    val id: String,
    val role: String,
    val company: String,
    val location: String,
    val status: ApplicationStatus,
    val dateApplied: String,
    val matchScore: Int,
    val isMatchScoreOutdated: Boolean = false,
    val nextAction: NextAction?,
    val jobDescriptionSnippet: String,
    val resumeUsed: String,
    val recruiter: String,
    val interviews: List<InterviewRound>,
    val notes: String
)

@HiltViewModel
class ApplicationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: JobApplicationRepository,
    private val resumeRepository: com.sandeep.aijobapplicationtracker.domain.repository.ResumeRepository
) : ViewModel() {

    private val jobId: String = savedStateHandle.get<String>("jobId") ?: ""

    private val _uiState = MutableStateFlow<UiState<JobDetailData>>(UiState.Loading)
    val uiState: StateFlow<UiState<JobDetailData>> = _uiState

    init {
        fetchJobDetail()
    }

    private fun fetchJobDetail() {
        viewModelScope.launch {
            kotlinx.coroutines.flow.combine(
                repository.getApplications(),
                resumeRepository.getResumes()
            ) { apps, resumes ->
                val app = apps.find { it.id == jobId }
                val primaryResume = resumes.find { it.isPrimary } ?: resumes.firstOrNull()
                Pair(app, primaryResume)
            }.collect { (app, primaryResume) ->
                if (app != null) {
                    val data = JobDetailData(
                        id = app.id,
                        role = app.jobTitle,
                        company = app.company,
                        location = app.location.ifBlank { "Not specified" },
                        status = mapStatus(app.status),
                        dateApplied = app.dateApplied.ifBlank { "-" },
                        matchScore = app.matchScore,
                        isMatchScoreOutdated = primaryResume != null && app.selectedResumeId.isNotBlank() && app.selectedResumeId != primaryResume.id,
                        nextAction = NextAction(
                            title = "AI Interview Preparation",
                            description = "Generate a custom interview plan based on the job description and your profile.",
                            isAiAction = true
                        ),
                        jobDescriptionSnippet = app.jobDescription.takeIf { it.isNotBlank() }?.take(100)?.plus("...") ?: "No JD provided.",
                        resumeUsed = primaryResume?.fileName ?: "No Resume Uploaded",
                        recruiter = app.recruiter,
                        interviews = app.interviews.map { 
                            InterviewRound(
                                title = it.roundNumber,
                                date = it.dateTime, // assuming they enter something like "Oct 15"
                                time = "", 
                                type = it.type
                            )
                        },
                        notes = app.notes.ifBlank { "No notes." }
                    )
                    _uiState.value = UiState.Success(data)
                } else {
                    _uiState.value = UiState.Error("Application not found.")
                }
            }
        }
    }

    private fun mapStatus(status: String): ApplicationStatus {
        val s = status.uppercase()
        return when {
            s.contains("SAVED") -> ApplicationStatus.SAVED
            s.contains("APPLI") -> ApplicationStatus.APPLIED
            s.contains("RECRUITER") -> ApplicationStatus.RECRUITER
            s.contains("INTERVIEW") -> ApplicationStatus.INTERVIEW
            s.contains("OFFER") -> ApplicationStatus.OFFER
            s.contains("REJECT") -> ApplicationStatus.REJECTED
            else -> ApplicationStatus.SAVED
        }
    }

    fun updateNotes(newNotes: String) {
        viewModelScope.launch {
            try {
                val app = repository.getApplications().first().find { it.id == jobId }
                if (app != null) {
                    repository.updateApplication(app.copy(notes = newNotes))
                }
            } catch (e: Exception) {
                // Ignore for now
            }
        }
    }

    fun deleteApplication() {
        viewModelScope.launch {
            try {
                repository.deleteApplication(jobId)
            } catch (e: Exception) {
                // Ignore for now
            }
        }
    }
}
