package com.sandeep.aijobapplicationtracker.presentation.screens.aiassistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository

enum class ActionType {
    FOLLOW_UP, PREPARE_INTERVIEW
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
    private val _uiState = MutableStateFlow<UiState<List<NextActionItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<NextActionItem>>> = _uiState

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
                
                applications.forEach { app ->
                    val status = app.status.lowercase()
                    if (status == "applied") {
                        dynamicActions.add(
                            NextActionItem(
                                id = app.id + "_followup",
                                company = app.company,
                                title = "Follow-up Recommended",
                                description = "You applied to ${app.jobTitle}. Consider sending a follow-up if you haven't heard back.",
                                actionType = ActionType.FOLLOW_UP,
                                jobId = app.id
                            )
                        )
                    } else if (status == "interview") {
                        dynamicActions.add(
                            NextActionItem(
                                id = app.id + "_prep",
                                company = app.company,
                                title = "Interview Prep",
                                description = "Recommended: Prepare for your upcoming ${app.jobTitle} interview.",
                                actionType = ActionType.PREPARE_INTERVIEW,
                                jobId = app.id
                            )
                        )
                    }
                }
                
                if (dynamicActions.isEmpty()) {
                    _uiState.value = UiState.Empty
                } else {
                    _uiState.value = UiState.Success(dynamicActions.take(5))
                }
            }
        }
    }
}
