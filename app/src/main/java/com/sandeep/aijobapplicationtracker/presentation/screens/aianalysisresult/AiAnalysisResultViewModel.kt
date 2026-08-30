package com.sandeep.aijobapplicationtracker.presentation.screens.aianalysisresult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExtractedJobData(
    val company: String,
    val role: String,
    val location: String,
    val experience: String,
    val seniority: String,
    val skills: List<String>
)

@HiltViewModel
class AiAnalysisResultViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<ExtractedJobData>>(UiState.Loading)
    val uiState: StateFlow<UiState<ExtractedJobData>> = _uiState

    init {
        loadExtractedData()
    }

    private fun loadExtractedData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            // Mocking the data passed from previous screen's AI extraction
            delay(1000)
            
            _uiState.value = UiState.Success(
                ExtractedJobData(
                    company = "XYZ Corporation",
                    role = "Senior Android Engineer",
                    location = "Bangalore",
                    experience = "6+ years",
                    seniority = "Senior",
                    skills = listOf("Kotlin", "Jetpack Compose", "Coroutines", "MVVM")
                )
            )
        }
    }

    fun confirmAndSave() {
        viewModelScope.launch {
            _uiState.value = UiState.Empty 
        }
    }
}
