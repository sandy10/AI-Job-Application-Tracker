package com.sandeep.aijobapplicationtracker.presentation.screens.aiinterviewprep

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FocusArea(
    val topic: String,
    val priority: String // e.g. "High priority", "Medium priority"
)

data class InterviewPlan(
    val company: String,
    val role: String,
    val focusAreas: List<FocusArea>,
    val likelyQuestions: List<String>
)

@HiltViewModel
class AiInterviewPrepViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val jobId: String = savedStateHandle.get<String>("jobId") ?: ""

    private val _uiState = MutableStateFlow<UiState<InterviewPlan>>(UiState.Loading)
    val uiState: StateFlow<UiState<InterviewPlan>> = _uiState

    init {
        generatePrepPlan()
    }

    private fun generatePrepPlan() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // Simulate Gemini AI plan generation
            delay(2000)
            
            val mockPlan = InterviewPlan(
                company = "Google",
                role = "Senior Android Developer",
                focusAreas = listOf(
                    FocusArea("Kotlin Coroutines", "High priority"),
                    FocusArea("Jetpack Compose", "High priority"),
                    FocusArea("Architecture (MVVM/Clean)", "Medium priority"),
                    FocusArea("System Design", "High priority")
                ),
                likelyQuestions = listOf(
                    "Explain structured concurrency.",
                    "How would you architect an offline-first Android application?",
                    "How do you prevent unnecessary Compose recomposition?",
                    "Design an application supporting 1M users."
                )
            )
            
            _uiState.value = UiState.Success(mockPlan)
        }
    }

    fun generateMoreQuestions() {
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            viewModelScope.launch {
                _uiState.value = UiState.Loading
                delay(1500) // Simulate AI working
                
                val currentPlan = currentState.data
                val newQuestions = currentPlan.likelyQuestions.toMutableList()
                newQuestions.add("What is the difference between launch and async?")
                newQuestions.add("Explain StateFlow vs SharedFlow.")
                
                _uiState.value = UiState.Success(currentPlan.copy(likelyQuestions = newQuestions))
            }
        }
    }
}
