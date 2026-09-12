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

import com.sandeep.aijobapplicationtracker.domain.model.ExtractedJobData

import com.sandeep.aijobapplicationtracker.domain.repository.AiAnalyzerRepository

@HiltViewModel
class AiAnalysisResultViewModel @Inject constructor(
    private val repository: AiAnalyzerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<ExtractedJobData>>(UiState.Loading)
    val uiState: StateFlow<UiState<ExtractedJobData>> = _uiState

    init {
        loadExtractedData()
    }

    private fun loadExtractedData() {
        viewModelScope.launch {
            repository.latestExtractedData.collect { data ->
                if (data != null) {
                    _uiState.value = UiState.Success(data)
                } else {
                    _uiState.value = UiState.Error("No data found")
                }
            }
        }
    }

    fun confirmAndSave() {
        viewModelScope.launch {
            _uiState.value = UiState.Empty 
        }
    }
}
