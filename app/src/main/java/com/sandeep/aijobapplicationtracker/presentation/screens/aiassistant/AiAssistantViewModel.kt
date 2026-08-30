package com.sandeep.aijobapplicationtracker.presentation.screens.aiassistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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
class AiAssistantViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<NextActionItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<NextActionItem>>> = _uiState

    init {
        fetchNextActions()
    }

    private fun fetchNextActions() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(1000)
            
            val mockActions = listOf(
                NextActionItem(
                    id = "1",
                    company = "ABC Technologies",
                    title = "Follow-up Required",
                    description = "Applied 7 days ago. No response recorded. Recommended: Send recruiter follow-up.",
                    actionType = ActionType.FOLLOW_UP,
                    jobId = "job_123"
                ),
                NextActionItem(
                    id = "2",
                    company = "Google",
                    title = "Interview Tomorrow",
                    description = "Recommended: Prepare Android System Design.",
                    actionType = ActionType.PREPARE_INTERVIEW,
                    jobId = "job_456"
                )
            )
            
            _uiState.value = UiState.Success(mockActions)
        }
    }
}
