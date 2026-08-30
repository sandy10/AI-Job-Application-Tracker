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
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    val nextAction: NextAction?,
    val jobDescriptionSnippet: String,
    val resumeUsed: String,
    val interviews: List<InterviewRound>,
    val notes: String
)

@HiltViewModel
class ApplicationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val jobId: String = savedStateHandle.get<String>("jobId") ?: ""

    private val _uiState = MutableStateFlow<UiState<JobDetailData>>(UiState.Loading)
    val uiState: StateFlow<UiState<JobDetailData>> = _uiState

    init {
        fetchJobDetail()
    }

    private fun fetchJobDetail() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // TODO: Fetch from repository using jobId
            delay(1000)
            
            // Mock data
            val mockData = JobDetailData(
                id = jobId,
                role = "Senior Android Developer",
                company = "Google",
                location = "Bangalore · Hybrid",
                status = ApplicationStatus.INTERVIEW,
                dateApplied = "24 Aug 2026",
                matchScore = 86,
                nextAction = NextAction(
                    title = "Interview in 2 days",
                    description = "Prepare:\n• Kotlin Coroutines\n• Jetpack Compose\n• System Design",
                    isAiAction = true
                ),
                jobDescriptionSnippet = "Looking for an experienced Android engineer with strong Kotlin, Compose, and architecture skills...",
                resumeUsed = "Android_Senior_v3.pdf",
                interviews = listOf(
                    InterviewRound("Round 1", "31 Aug", "11:00 AM", "Technical")
                ),
                notes = "Recruiter mentioned focus on Compose."
            )
            
            _uiState.value = UiState.Success(mockData)
        }
    }
}
