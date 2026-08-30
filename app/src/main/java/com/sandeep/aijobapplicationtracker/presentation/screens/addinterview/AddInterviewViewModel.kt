package com.sandeep.aijobapplicationtracker.presentation.screens.addinterview

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

@HiltViewModel
class AddInterviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val jobId: String = savedStateHandle.get<String>("jobId") ?: ""

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    fun saveInterview(
        roundNumber: String,
        type: String,
        dateTime: String,
        meetingUrl: String,
        interviewer: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(100)
            _uiState.value = UiState.Success(Unit)
        }
    }
}
