package com.sandeep.aijobapplicationtracker.presentation.screens.aiassistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository

import java.util.Date
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

enum class ActionType {
    APPLY, FOLLOW_UP, PREPARE_INTERVIEW, THANK_YOU, REVIEW_OFFER
}

data class NextActionItem(
    val id: String,
    val company: String,
    val title: String,
    val description: String,
    val actionType: ActionType,
    val jobId: String
)

@HiltViewModel
class AiAssistantViewModel @Inject constructor(
    private val jobRepository: JobApplicationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<ImmutableList<NextActionItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<ImmutableList<NextActionItem>>> = _uiState

    val latestJobId: StateFlow<String?> = jobRepository.getApplications()
        .map { apps -> apps.firstOrNull()?.id }
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), null)

    init {
        fetchNextActions()
    }

    private fun fetchNextActions() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            jobRepository.getApplications().collect { applications ->
                val dynamicActions = mutableListOf<NextActionItem>()
                

                val currentTime = System.currentTimeMillis()
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val interviewFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())

                applications.forEach { app ->
                    val status = app.status.lowercase()
                    
                    // Rule 1: Saved > 3 days -> Apply
                    if (status == "saved") {
                        val daysSinceSaved = (currentTime - app.timestamp) / (1000 * 60 * 60 * 24)
                        if (daysSinceSaved > 3) {
                            dynamicActions.add(
                                NextActionItem(
                                    id = app.id + "_apply",
                                    company = app.company,
                                    title = "Apply Now",
                                    description = "You saved ${app.jobTitle} $daysSinceSaved days ago. It's time to apply!",
                                    actionType = ActionType.APPLY,
                                    jobId = app.id
                                )
                            )
                        }
                    }
                    
                    // Rule 2: Applied > 5 days -> Follow up
                    if (status == "applied") {
                        try {
                            val appliedDate = dateFormat.parse(app.dateApplied)
                            if (appliedDate != null) {
                                val daysSinceApplied = (currentTime - appliedDate.time) / (1000 * 60 * 60 * 24)
                                if (daysSinceApplied > 5) {
                                    dynamicActions.add(
                                        NextActionItem(
                                            id = app.id + "_followup",
                                            company = app.company,
                                            title = "Follow-up Recommended",
                                            description = "You applied to ${app.jobTitle} $daysSinceApplied days ago. Send a follow-up email.",
                                            actionType = ActionType.FOLLOW_UP,
                                            jobId = app.id
                                        )
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            // Date parse failed, default fallback if needed
                        }
                    }
                    
                    // Rule 3: Interview tomorrow -> Prepare interview
                    // Rule 4: Interview completed -> Send thank-you / follow-up
                    if (app.interviews.isNotEmpty()) {
                        try {
                            // Find the most relevant interview
                            val sortedInterviews = app.interviews.mapNotNull {
                                val date = interviewFormat.parse(it.dateTime)
                                if (date != null) Pair(it, date) else null
                            }.sortedBy { it.second.time }
                            
                            val nextInterview = sortedInterviews.firstOrNull { it.second.time > currentTime }
                            val pastInterview = sortedInterviews.lastOrNull { it.second.time <= currentTime }
                            
                            if (nextInterview != null) {
                                val daysUntil = (nextInterview.second.time - currentTime) / (1000 * 60 * 60 * 24)
                                if (daysUntil <= 1) {
                                    dynamicActions.add(
                                        NextActionItem(
                                            id = app.id + "_prep",
                                            company = app.company,
                                            title = "Interview Tomorrow",
                                            description = "Prepare for your ${nextInterview.first.type} interview for ${app.jobTitle}.",
                                            actionType = ActionType.PREPARE_INTERVIEW,
                                            jobId = app.id
                                        )
                                    )
                                }
                            } else if (pastInterview != null && status == "interview") {
                                // Last interview passed, but no offer yet
                                val daysSince = (currentTime - pastInterview.second.time) / (1000 * 60 * 60 * 24)
                                if (daysSince in 1..7) {
                                    dynamicActions.add(
                                        NextActionItem(
                                            id = app.id + "_thankyou",
                                            company = app.company,
                                            title = "Send Thank-You Note",
                                            description = "You completed an interview for ${app.jobTitle}. Send a thank-you note.",
                                            actionType = ActionType.THANK_YOU,
                                            jobId = app.id
                                        )
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            // Ignore parse errors
                        }
                    }
                    
                    // Rule 5: Offer received -> Review offer
                    if (status == "offer received" || status == "offer") {
                        dynamicActions.add(
                            NextActionItem(
                                id = app.id + "_offer",
                                company = app.company,
                                title = "Review Offer",
                                description = "Congratulations on the offer for ${app.jobTitle}! Take time to review it.",
                                actionType = ActionType.REVIEW_OFFER,
                                jobId = app.id
                            )
                        )
                    }
                }

                
                if (dynamicActions.isEmpty()) {
                    _uiState.value = UiState.Empty
                } else {
                    _uiState.value = UiState.Success(dynamicActions.take(5).toImmutableList())
                }
            }
        }
    }
}

