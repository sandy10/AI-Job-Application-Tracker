package com.sandeep.aijobapplicationtracker.presentation.screens.addapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.aijobapplicationtracker.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddApplicationViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    fun saveApplication(
        company: String,
        jobTitle: String,
        jobUrl: String,
        workMode: String,
        status: String,
        notes: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(100)
            _uiState.value = UiState.Success(Unit)
        }
    }

    fun analyzeWithAI() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // TODO: Implement actual Gemini AI call and extraction
            delay(2000) // Simulating AI thinking
            
            // In reality, this would transition to the AI Analyzer screen or return extracted data.
            // For now we'll just show an error indicating it's not fully wired yet
            _uiState.value = UiState.Error("AI Analyzer not fully implemented in MVP UI yet")
            delay(2000)
            _uiState.value = UiState.Idle
        }
    }
}
