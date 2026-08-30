package com.sandeep.aijobapplicationtracker.presentation.screens.aijobanalyzer

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
class AiJobAnalyzerViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    fun analyzeJobDescription(jobDescription: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(100)
            _uiState.value = UiState.Success(Unit)
        }
    }
}
